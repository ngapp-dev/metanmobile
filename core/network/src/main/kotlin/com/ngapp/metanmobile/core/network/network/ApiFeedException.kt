/*
 * Copyright 2026 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.core.network.network

/**
 * Thrown when the Metan Ecogas API responds successfully (HTTP 200) but reports that the
 * requested feed could not be refreshed on the server and it has no cached data to fall back on
 * (empty `data` with a non-null `error`). This lets a genuinely failed feed refresh be treated as
 * a sync failure instead of silently looking like "there is no content".
 */
class ApiFeedException(message: String) : Exception(message)
