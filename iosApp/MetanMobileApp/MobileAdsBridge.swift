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
