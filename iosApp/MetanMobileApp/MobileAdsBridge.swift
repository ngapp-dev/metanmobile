import Foundation
import UIKit
import GoogleMobileAds
import UserMessagingPlatform
import YandexMobileAds
import MetanMobileComposeApp

/// Swift-side implementation of the Kotlin `NativeAdsBridge` protocol. The SDKs are linked by
/// this Xcode project's Swift Package dependencies and are invisible to the Kotlin/Native
/// framework, so this bridge owns the platform-specific network choice and views.
final class MobileAdsBridge: NSObject, NativeAdsBridge {

    func initializeMobileAdsSdk() {
        if Self.isRussianAudience {
            initializeYandexMobileAdsSdk()
        } else {
            MobileAds.shared.start(completionHandler: nil)
        }
    }

    func isPrivacyOptionsRequired() -> Bool {
        ConsentInformation.shared.privacyOptionsRequirementStatus == .required
    }

    func updateConsent(onResult: @escaping (KotlinBoolean) -> Void) {
        guard let presenter = Self.topViewController() else {
            finish(onResult: onResult)
            return
        }
        ConsentForm.presentPrivacyOptionsForm(from: presenter) { [weak self] _ in
            self?.finish(onResult: onResult)
        }
    }

    func obtainConsentAndShow(onResult: @escaping (KotlinBoolean) -> Void) {
        let parameters = RequestParameters()
        parameters.isTaggedForUnderAgeOfConsent = false

        ConsentInformation.shared.requestConsentInfoUpdate(with: parameters) { [weak self] error in
            guard let self else { return }
            if error != nil {
                // Consent info couldn't be fetched (offline, misconfigured AdMob app id, ...) -
                // matches Android's ConsentHelper, which only flips canShowAds on a successful
                // update. finish() re-checks canRequestAds itself, so this still resolves safely.
                self.finish(onResult: onResult)
                return
            }
            guard let presenter = Self.topViewController() else {
                self.finish(onResult: onResult)
                return
            }
            ConsentForm.loadAndPresentIfRequired(from: presenter) { [weak self] _ in
                self?.finish(onResult: onResult)
            }
        }
    }

    func revokeConsent() {
        ConsentInformation.shared.reset()
    }

    // Both banner SDKs keep delegates weak. Retain each coordinator for the lifetime of its view.
    private var bannerDelegates: [ObjectIdentifier: AnyObject] = [:]
    private var isYandexInitialized = false
    private var isYandexInitializationInProgress = false
    private var pendingYandexInitializationHandlers: [() -> Void] = []

    func makeBannerAdView(adUnitId: String, onAdLoadResult: @escaping (KotlinBoolean) -> Void) -> UIView {
        if Self.isRussianAudience {
            return makeYandexBannerAdView(onAdLoadResult: onAdLoadResult)
        }

        return makeGoogleBannerAdView(adUnitId: adUnitId, onAdLoadResult: onAdLoadResult)
    }

    private func makeGoogleBannerAdView(
        adUnitId: String,
        onAdLoadResult: @escaping (KotlinBoolean) -> Void
    ) -> UIView {
        let bannerView = BannerView(adSize: AdSizeBanner)
        bannerView.adUnitID = adUnitId
        bannerView.rootViewController = Self.topViewController()

        let coordinator = GoogleBannerAdCoordinator { loaded in
            onAdLoadResult(KotlinBoolean(bool: loaded))
        }
        bannerView.delegate = coordinator
        bannerDelegates[ObjectIdentifier(bannerView)] = coordinator

        bannerView.load(Request())
        return bannerView
    }

    private func makeYandexBannerAdView(
        onAdLoadResult: @escaping (KotlinBoolean) -> Void
    ) -> UIView {
        let bannerView = YandexMobileAds.BannerAdView(
            adSize: BannerAdSize.fixed(width: 320, height: 50)
        )
        bannerView.frame = CGRect(x: 0, y: 0, width: 320, height: 50)

        let coordinator = MainActor.assumeIsolated {
            YandexBannerAdCoordinator { loaded in
                onAdLoadResult(KotlinBoolean(bool: loaded))
            }
        }
        bannerView.delegate = coordinator
        bannerDelegates[ObjectIdentifier(bannerView)] = coordinator

        // The request is issued only after the Yandex SDK initialization callback. A failure
        // remains hidden by Kotlin's MainBannerAd and is deliberately not retried here.
        initializeYandexMobileAdsSdk { [weak bannerView] in
            guard let bannerView else { return }
            bannerView.loadAd(
                with: YandexMobileAds.AdRequest(adUnitID: Self.yandexRuBannerAdUnitId)
            )
        }
        return bannerView
    }

    // MARK: - NativeBanner

    /// Loaded native ads per placement, kept for the app's lifetime (see `makeNativeAdView`).
    private var googleNativeAds: [String: GoogleMobileAds.NativeAd] = [:]
    private var yandexNativeAds: [String: any YandexMobileAds.NativeAd] = [:]
    /// Loaders and their delegates must outlive the request.
    private var nativeAdLoads: [String: AnyObject] = [:]
    private lazy var yandexNativeAdLoader = YandexMobileAds.NativeAdLoader()

    func makeNativeAdView(
        slotKey: String,
        style: NativeAdViewStyle,
        onAdLoadResult: @escaping (KotlinDouble) -> Void
    ) -> UIView {
        let container = UIView()
        let report: (CGFloat) -> Void = { height in
            // Async: Compose may still be inside the composition that created this view.
            DispatchQueue.main.async { onAdLoadResult(KotlinDouble(double: Double(height))) }
        }

        if Self.isRussianAudience {
            let show: (any YandexMobileAds.NativeAd) -> Void = { ad in
                let row = MainActor.assumeIsolated { NativeBannerRow.yandex(ad: ad, style: style) }
                container.embed(row.view)
                report(row.height)
            }
            if let ad = yandexNativeAds[slotKey] {
                show(ad)
            } else {
                initializeYandexMobileAdsSdk { [weak self] in
                    guard let self else { return }
                    self.yandexNativeAdLoader.loadAd(
                        with: YandexMobileAds.AdRequest(adUnitID: Self.yandexRuNativeBannerAdUnitId)
                    ) { [weak self] ad, _ in
                        // A failed slot simply stays empty - no retry loop.
                        guard let ad else { return report(0) }
                        self?.yandexNativeAds[slotKey] = ad
                        show(ad)
                    }
                }
            }
            return container
        }

        let show: (GoogleMobileAds.NativeAd) -> Void = { ad in
            let row = MainActor.assumeIsolated { NativeBannerRow.google(ad: ad, style: style) }
            container.embed(row.view)
            report(row.height)
        }
        if let ad = googleNativeAds[slotKey] {
            show(ad)
        } else if nativeAdLoads[slotKey] == nil {
            let options = NativeAdMediaAdLoaderOptions()
            // The row thumbnail is square; ask for media that fits it.
            options.mediaAspectRatio = .square
            let loader = AdLoader(
                adUnitID: Self.nativeBannerAdUnitId,
                rootViewController: Self.topViewController(),
                adTypes: [.native],
                options: [options]
            )
            let delegate = GoogleNativeAdLoaderDelegate { [weak self] ad in
                self?.nativeAdLoads[slotKey] = nil
                guard let ad else { return report(0) }
                self?.googleNativeAds[slotKey] = ad
                show(ad)
            }
            loader.delegate = delegate
            nativeAdLoads[slotKey] = [loader, delegate] as NSArray
            loader.load(Request())
        }
        return container
    }

    private func initializeYandexMobileAdsSdk(completion: @escaping () -> Void = {}) {
        if isYandexInitialized {
            completion()
            return
        }

        pendingYandexInitializationHandlers.append(completion)
        guard !isYandexInitializationInProgress else { return }

        isYandexInitializationInProgress = true
        YandexAds.initializeSDK { [weak self] in
            guard let self else { return }
            self.isYandexInitialized = true
            self.isYandexInitializationInProgress = false
            let handlers = self.pendingYandexInitializationHandlers
            self.pendingYandexInitializationHandlers.removeAll()
            handlers.forEach { $0() }
        }
    }

    /// Mirrors Android ConsentHelper's handleConsentResult(): only flips canShowAds - and only
    /// then initializes the Mobile Ads SDK - once UMP actually allows requesting ads.
    private func finish(onResult: @escaping (KotlinBoolean) -> Void) {
        let canShow = ConsentInformation.shared.canRequestAds
        if canShow {
            initializeMobileAdsSdk()
        }
        onResult(KotlinBoolean(bool: canShow))
    }

    private static func topViewController() -> UIViewController? {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow }?.rootViewController
    }

    private static var isRussianAudience: Bool {
        Locale.current.region?.identifier.uppercased() == "RU"
    }

    private static var nativeBannerAdUnitId: String {
        guard let adUnitId = Bundle.main.object(forInfoDictionaryKey: "MMNativeBannerAdUnitID") as? String,
              !adUnitId.isEmpty,
              !adUnitId.hasPrefix("$") else {
            // Google's public iOS native advanced test unit.
            return "ca-app-pub-3940256099942544/3986624511"
        }
        return adUnitId
    }

    private static var yandexRuNativeBannerAdUnitId: String {
        guard let adUnitId = Bundle.main.object(forInfoDictionaryKey: "MMYandexRuNativeBannerAdUnitID") as? String,
              !adUnitId.isEmpty,
              !adUnitId.hasPrefix("$") else {
            return "demo-native-app-yandex"
        }
        return adUnitId
    }

    private static var yandexRuBannerAdUnitId: String {
        guard let adUnitId = Bundle.main.object(forInfoDictionaryKey: "MMYandexRuBannerAdUnitID") as? String,
              !adUnitId.isEmpty,
              !adUnitId.hasPrefix("$") else {
            return "demo-banner-yandex"
        }
        return adUnitId
    }
}

/// Reports a `BannerView`'s load result back up through `NativeAdsBridge.makeBannerAdView`'s
/// callback - `MainBannerAd.ios.kt` only shows (and reserves layout space for) the banner once
/// this fires true, same as Android's own AdListener-driven `MainBannerAd.android.kt`.
private final class GoogleBannerAdCoordinator: NSObject, BannerViewDelegate {
    private let onResult: (Bool) -> Void

    init(onResult: @escaping (Bool) -> Void) {
        self.onResult = onResult
    }

    func bannerViewDidReceiveAd(_ bannerView: BannerView) {
        onResult(true)
    }

    func bannerView(_ bannerView: BannerView, didFailToReceiveAdWithError error: Error) {
        onResult(false)
    }
}

private final class YandexBannerAdCoordinator: NSObject, YandexMobileAds.BannerAdViewDelegate {
    private let onResult: (Bool) -> Void

    init(onResult: @escaping (Bool) -> Void) {
        self.onResult = onResult
    }

    func bannerAdViewDidLoad(_ bannerAdView: YandexMobileAds.BannerAdView) {
        onResult(true)
    }

    func bannerAdViewDidFailLoading(_ bannerAdView: YandexMobileAds.BannerAdView, error: Error) {
        onResult(false)
    }

    func bannerAdViewDidClick(_ bannerAdView: YandexMobileAds.BannerAdView) {}

    func bannerAdView(
        _ bannerAdView: YandexMobileAds.BannerAdView,
        didTrackImpression impressionData: YandexMobileAds.ImpressionData?
    ) {}
}

private final class GoogleNativeAdLoaderDelegate: NSObject, NativeAdLoaderDelegate {
    private let onResult: (GoogleMobileAds.NativeAd?) -> Void

    init(onResult: @escaping (GoogleMobileAds.NativeAd?) -> Void) {
        self.onResult = onResult
    }

    func adLoader(_ adLoader: AdLoader, didReceive nativeAd: GoogleMobileAds.NativeAd) {
        onResult(nativeAd)
    }

    func adLoader(_ adLoader: AdLoader, didFailToReceiveAdWithError error: Error) {
        onResult(nil)
    }
}

private extension UIView {
    /// Pins [child] to this view's edges (inset by [insets]), on top of existing subviews.
    func embed(_ child: UIView, insets: UIEdgeInsets = .zero) {
        child.translatesAutoresizingMaskIntoConstraints = false
        addSubview(child)
        NSLayoutConstraint.activate([
            child.leadingAnchor.constraint(equalTo: leadingAnchor, constant: insets.left),
            child.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -insets.right),
            child.topAnchor.constraint(equalTo: topAnchor, constant: insets.top),
            child.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -insets.bottom),
        ])
    }
}

// MARK: - NativeBanner row

/// The NativeBanner row, matching the app's NewsRow / StationRow: thumbnail on the left (the
/// icon gets the rows' asymmetric-rounded image shape; media stays square-cornered so the creative
/// and its video controls are never clipped), title (+ description) and a meta line with the "Ad"
/// label, advertiser and call-to-action. With an icon it's a list row's size (82pt); showing media
/// it grows to fit a 120x120pt thumbnail, the minimum media size.
@MainActor
private enum NativeBannerRow {
    struct Built {
        let view: UIView
        let height: CGFloat
    }

    private struct Parts {
        let thumbnail = UIView()
        let icon = UIImageView()
        let title = UILabel()
        let description = UILabel()
        let sponsored = InsetLabel()
        let favicon = UIImageView()
        let domain = UILabel()
        let age = UILabel()
        let callToAction = UIButton(type: .custom)
        let warning = UILabel()
        let feedback = UIButton(type: .custom)
    }

    static func google(ad: GoogleMobileAds.NativeAd, style: NativeAdViewStyle) -> Built {
        let adView = GoogleMobileAds.NativeAdView()
        let media = GoogleMobileAds.MediaView()
        media.contentMode = .scaleAspectFill
        let parts = Parts()
        let hasIcon = ad.icon?.image != nil
        let height = layout(in: adView, parts: parts, media: media, style: style, mediaSized: !hasIcon, withFeedback: false)

        parts.title.text = ad.headline
        parts.description.text = ad.body
        parts.description.isHidden = ad.body?.isEmpty ?? true || (!style.isStationLayout && hasIcon)
        parts.domain.text = ad.advertiser
        parts.sponsored.text = style.adLabel
        parts.callToAction.setTitle(ad.callToAction, for: .normal)
        parts.callToAction.isHidden = ad.callToAction == nil
        // The native ad view handles clicks itself.
        parts.callToAction.isUserInteractionEnabled = false
        parts.icon.image = ad.icon?.image
        parts.icon.isHidden = !hasIcon
        media.isHidden = hasIcon
        [parts.age, parts.warning, parts.favicon, parts.feedback].forEach { $0.isHidden = true }

        adView.headlineView = parts.title
        adView.bodyView = parts.description
        adView.advertiserView = parts.domain
        adView.callToActionView = parts.callToAction
        adView.iconView = parts.icon
        adView.mediaView = media
        adView.nativeAd = ad
        return Built(view: adView, height: height)
    }

    static func yandex(ad: any YandexMobileAds.NativeAd, style: NativeAdViewStyle) -> Built {
        let adView = YandexMobileAds.NativeAdView()
        let media = YandexMobileAds.NativeMediaView()
        let parts = Parts()
        // Any Yandex ad may carry media, so the thumbnail always has media size.
        let height = layout(in: adView, parts: parts, media: media, style: style, mediaSized: true, withFeedback: true)

        adView.titleLabel = parts.title
        adView.bodyLabel = parts.description
        adView.sponsoredLabel = parts.sponsored
        adView.domainLabel = parts.domain
        adView.faviconImageView = parts.favicon
        adView.ageLabel = parts.age
        adView.callToActionButton = parts.callToAction
        adView.warningLabel = parts.warning
        adView.feedbackButton = parts.feedback
        adView.iconImageView = parts.icon
        adView.mediaView = media
        // Yandex fills every bound view itself (hiding the ones the ad has no asset for).
        try? ad.bind(with: adView)
        return Built(view: adView, height: height)
    }

    private static func layout(
        in root: UIView,
        parts: Parts,
        media: UIView,
        style: NativeAdViewStyle,
        mediaSized: Bool,
        withFeedback: Bool
    ) -> CGFloat {
        root.backgroundColor = UIColor(argb: style.background)

        let thumbnailWidth: CGFloat = mediaSized ? 120 : 74
        let thumbnailHeight: CGFloat = mediaSized ? 120 : 66
        let rowHeight = thumbnailHeight + 16

        parts.thumbnail.translatesAutoresizingMaskIntoConstraints = false
        [media, parts.icon].forEach { child in
            parts.thumbnail.embed(child)
        }
        parts.icon.contentMode = .scaleAspectFill
        parts.icon.clipsToBounds = true
        // RoundedCornerShape(20.dp, 0.dp, 20.dp, 0.dp): top-left and bottom-right.
        parts.icon.layer.cornerRadius = 20
        parts.icon.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMaxYCorner]
        media.clipsToBounds = true

        parts.title.font = .systemFont(ofSize: style.titleSize, weight: .bold)
        parts.title.textColor = UIColor(argb: style.titleColor)
        parts.title.numberOfLines = style.isStationLayout ? 1 : 2
        parts.description.font = .systemFont(ofSize: style.descriptionSize, weight: .medium)
        parts.description.textColor = UIColor(argb: style.descriptionColor)
        parts.description.numberOfLines = mediaSized ? 2 : 1
        parts.sponsored.font = .systemFont(ofSize: style.metaSize)
        parts.sponsored.textColor = UIColor(argb: style.onAccent)
        parts.sponsored.backgroundColor = UIColor(argb: style.accent)
        parts.sponsored.layer.cornerRadius = 4
        parts.sponsored.clipsToBounds = true
        [parts.domain, parts.age, parts.warning].forEach {
            $0.font = .systemFont(ofSize: style.metaSize)
            $0.textColor = UIColor(argb: style.metaColor)
        }
        parts.domain.lineBreakMode = .byTruncatingTail
        parts.domain.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        parts.callToAction.titleLabel?.font = .systemFont(ofSize: style.metaSize + 1, weight: .bold)
        parts.callToAction.setTitleColor(UIColor(argb: style.accent), for: .normal)
        parts.callToAction.setContentHuggingPriority(.required, for: .horizontal)
        parts.callToAction.setContentCompressionResistancePriority(.required, for: .horizontal)

        let meta = UIStackView(arrangedSubviews: [parts.sponsored, parts.favicon, parts.domain, parts.age, parts.callToAction])
        meta.axis = .horizontal
        meta.alignment = .center
        meta.spacing = 6
        let texts = UIStackView(arrangedSubviews: [parts.title, parts.description, meta, parts.warning])
        texts.axis = .vertical
        texts.spacing = 4
        texts.setCustomSpacing(6, after: parts.description)

        var rowViews: [UIView] = [parts.thumbnail, texts]
        if withFeedback { rowViews.append(parts.feedback) }
        let row = UIStackView(arrangedSubviews: rowViews)
        row.axis = .horizontal
        row.alignment = .center
        row.spacing = mediaSized ? 12 : 8
        root.embed(row, insets: UIEdgeInsets(top: 8, left: 16, bottom: 8, right: 12))

        var constraints = [
            parts.thumbnail.widthAnchor.constraint(equalToConstant: thumbnailWidth),
            parts.thumbnail.heightAnchor.constraint(equalToConstant: thumbnailHeight),
            parts.favicon.widthAnchor.constraint(equalToConstant: 12),
            parts.favicon.heightAnchor.constraint(equalToConstant: 12),
        ]
        if withFeedback {
            // Yandex: icons at least 32x32pt.
            constraints += [
                parts.feedback.widthAnchor.constraint(equalToConstant: 32),
                parts.feedback.heightAnchor.constraint(equalToConstant: 32),
            ]
        }
        NSLayoutConstraint.activate(constraints)
        return rowHeight
    }
}

/// A label with a little horizontal padding, for the "Ad" badge.
private final class InsetLabel: UILabel {
    private let insets = UIEdgeInsets(top: 0, left: 4, bottom: 0, right: 4)

    override func drawText(in rect: CGRect) {
        super.drawText(in: rect.inset(by: insets))
    }

    override var intrinsicContentSize: CGSize {
        let size = super.intrinsicContentSize
        return CGSize(width: size.width + insets.left + insets.right, height: size.height)
    }
}

private extension UIColor {
    convenience init(argb: Int64) {
        let value = UInt32(truncatingIfNeeded: argb)
        self.init(
            red: CGFloat((value >> 16) & 0xFF) / 255,
            green: CGFloat((value >> 8) & 0xFF) / 255,
            blue: CGFloat(value & 0xFF) / 255,
            alpha: CGFloat((value >> 24) & 0xFF) / 255
        )
    }
}
