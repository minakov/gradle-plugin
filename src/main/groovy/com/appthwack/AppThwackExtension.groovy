/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appthwack

class AppThwackExtension {

    public static final String JUNIT_TYPE = "junit"
    public static final String CALABASH_TYPE = "calabash"
    public static final String APP_EXPLORER_TYPE = "appexplorer"

    /**
     * [Internal] Set the domain of the API endpoint. Used for testing.
     * Default: 'https://appthwack.com'
     */
    String domain = "https://appthwack.com"

    /**
     * [Required] API Key for AppThwack user account.
     * See: https://appthwack.com/user/profile for more details.
     */
    String apiKey

    /**
     * [Required] Test type. Supports: calabash, junit, appexplorer
     * Default: junit
     */
    String type = JUNIT_TYPE

    /**
     * [Required] Name of AppThwack project which contains this application.
     */
    String project

    /**
     * [Optional] Name of test run to be displayed on AppThwack.
     * Default: <Name of APK (Gradle)>
     */
    String name

    /**
     * [Optional] Name of custom device pool to use.
     * Default: Top 10
     */
    String devicePool = "Top 10"

    /**
     * [Optional] Fully qualified path to features.zip which contains Calabash test content.
     * Note: Set by call to calabash()
     */
    String calabashContent

    /**
     * [Optional] Optional execution parameters for the AppThwack AppExplorer.
     * Note: Set by call to explorer()
     */
    public final HashMap<String, String> explorerOptions = new HashMap<String, String>();

    /**
     * Select Calabash content to use.
     * @param path path to features.zip
     */
    void calabash(String path) {
        type = CALABASH_TYPE
        calabashContent = path
    }

    /**
     * Default basic AppThwack AppExplorer.
     * @param username custom username for login credentials
     * @param password customer password for login credentials
     * @param launchData URI passed to the app
     * @param eventCount number of monkey exerciser events
     * @param monkeySeed random seed used by monkey exerciser
     */
    void explorer(String username="",
                  String password="",
                  String launchData="",
                  int eventCount=0,
                  int monkeySeed=0) {

        type = APP_EXPLORER_TYPE
        explorerOptions.put("username", username)
        explorerOptions.put("password", password)
        explorerOptions.put("launchdata", launchData)
        explorerOptions.put("eventcount", Integer.toString(eventCount))
        explorerOptions.put("monkeyseed", Integer.toString(monkeySeed))
    }
}
