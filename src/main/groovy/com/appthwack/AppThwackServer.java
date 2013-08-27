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

package com.appthwack;

import java.io.File;
import java.util.HashMap;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.builder.testing.api.TestServer;
import com.android.utils.ILogger;
import com.appthwack.AppThwackPlugin;
import com.appthwack.appthwack.*;


public class AppThwackServer extends TestServer {

    private final AppThwackExtension extension;
    private final ILogger logger;
    private AppThwackApi api;

    AppThwackServer(@NonNull AppThwackExtension extension,
                    @NonNull ILogger logger) {
        this.extension = extension;
        this.logger = logger;
    }

    /**
     * Name of the gradle plugin.
     * @return appthwack
     */
    @Override
    public String getName() {
        return AppThwackPlugin.PLUGIN_NAME;
    }

    /**
     * Upload and test the newly built apk.
     * @param variantName variant of the latest build. Ex: 'debug'
     * @param testApk File object to the newly built APK which contains tests
     * @param testedApk File object to the newly built application APK
     */
    @Override
    public void uploadApks(@NonNull String variantName, @NonNull File testApk, @Nullable File testedApk) {
        if (testedApk == null) {
            logger.warning("[AppThwack] Application APK required.");
            return;
        }

        //Configure client to use user defined API Key.
        String apiKey = extension.getApiKey();
        String domain = extension.getDomain();
        api = new AppThwackApi(apiKey, domain);

        String projectName = extension.getProject();
        System.out.println(String.format("[AppThwack] Using Project '%s'.", projectName));

        //Attempt to get AppThwack project with user defined name.
        AppThwackProject project = api.getProject(projectName);
        if (project == null) {
            logger.warning("[AppThwack] Project '%s' not found.", projectName);
            return;
        }

        String devicePoolName = extension.getDevicePool();
        System.out.println(String.format("[AppThwack] Using DevicePool '%s'.", devicePoolName));

        //Attempt to get AppThwack device pool with user defined name.
        AppThwackDevicePool devicePool = project.getDevicePool(devicePoolName);
        if (devicePool == null) {
            logger.warning("[AppThwack] Device Pool '%s' not found.", devicePoolName);
            return;
        }

        System.out.println(String.format("[AppThwack] Uploading apk '%s'.", testedApk.getName()));

        //Upload APK.
        AppThwackFile app = uploadFile(testedApk);
        if (app == null) {
            logger.warning("[AppThwack] Failed to upload apk '%s'.", testedApk.getName());
            return;
        }

        //Upload test content.
        AppThwackFile tests = uploadTestContent(testApk);

        String type = extension.getType();
        if (tests == null && !type.equalsIgnoreCase(AppThwackExtension.APP_EXPLORER_TYPE)) {
            logger.warning("[AppThwack] Unable to schedule run, failed to upload required test content.");
            return;
        }

        //Test name default is <Apk Name (Gradle)>
        String name = extension.getName();
        if (name == null || name.isEmpty()) {
            name = String.format("%s (Gradle)", testedApk.getName());
        }
        System.out.println(String.format("[AppThwack] Scheduling '%s' run '%s'.", type, name));

        //Schedule our test run.
        AppThwackRun run = scheduleTestRun(project, devicePool, type, name, app, tests, extension.explorerOptions);
        if (run == null) {
            logger.warning("[AppThwack] Failed to schedule test run '%s'.", name);
            return;
        }

        System.out.println(String.format("[AppThwack] Congrats! See your test results at %s/%s.", domain, run.toString()));
    }

    /**
     * Verify the AppThwack extension is properly configured.
     * @return true if configuration is valid, false otherwise.
     */
    @Override
    public boolean isConfigured() {

        //[Required]: AppThwack Api Key.
        String apiKey = extension.getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            logger.warning("AppThwack apiKey is required. See https://appthwack.com/user/profile for details.");
            return false;
        }

        //[Required]: AppThwack project name.
        String project = extension.getProject();
        if (project == null || project.isEmpty()) {
            logger.warning("[AppThwack] project name is required.");
            return false;
        }

        //[Required]: AppThwack test runner type. Defaults to junit.
        String type = extension.getType();
        if (type == null || type.isEmpty()) {
            logger.warning("[AppThwack] type is required. Expects: junit, calabash, appexplorer.");
            return false;
        }
        if (!(type.equalsIgnoreCase(AppThwackExtension.JUNIT_TYPE)
                || type.equalsIgnoreCase(AppThwackExtension.CALABASH_TYPE)
                || type.equalsIgnoreCase(AppThwackExtension.APP_EXPLORER_TYPE))) {
            logger.warning("[AppThwack] Type is invalid. Expects: junit, calabash, appexplorer.");
            return false;
        }
        return true;
    }

    /**
     * Upload JUnit/Robotium test apk or Calabash scripts.
     * @param testApk test apk generated by the build
     * @return object which represents a remote file on AppThwack
     */
    private AppThwackFile uploadTestContent(File testApk) {

        String type = extension.getType();

        //Upload JUnit/Robotium test content (.apk).
        if (type.equalsIgnoreCase(AppThwackExtension.JUNIT_TYPE)) {
            if (testApk == null) {
                logger.warning("[AppThwack] No test apk provided. Unable to run JUnit/Robotium tests.");
                return null;
            }
            System.out.println(String.format("[AppThwack] Uploading test apk '%s'.", testApk.getName()));

            AppThwackFile testApkFile = uploadFile(testApk);
            if (testApkFile == null) {
                logger.warning("[AppThwack] Failed to upload test apk '%s'.", testApk.getName());
                return null;
            }
            return testApkFile;
        }

        //Upload Calabash test content (.zip).
        if (type.equalsIgnoreCase(AppThwackExtension.CALABASH_TYPE)) {
            String calabashPath = extension.getCalabashContent();
            if (calabashPath == null || calabashPath.isEmpty()) {
                logger.warning("[AppThwack] No content provided. Unable to run Calabash tests.");
                return null;
            }
            File content = new File(calabashPath);
            if (!content.exists()) {
                logger.warning("[AppThwack] Calabash content not found at '%s'.", content.getAbsolutePath());
                return null;
            }
            if (!calabashPath.endsWith(".zip")) {
                logger.warning("[AppThwack] Calabash content must be of type .zip.");
                return null;
            }
            System.out.println(String.format("[AppThwack] Uploading Calabash test content '%s'.", content.getName()));

            AppThwackFile calabashTests = uploadFile(content);
            if (calabashTests == null) {
                logger.warning("[AppThwack] Failed to upload Calabash content '%s'.", content.getAbsolutePath());
                return null;
            }
            return calabashTests;
        }
        return null;
    }

    /**
     * Schedules a test run on AppThwack.
     * @param project user project which will contain the run
     * @param pool device pool to run tests on
     * @param type type of tests to run
     * @param name name of test run
     * @param app object returned from uploading user apk
     * @param tests object returned from uploading user test content
     * @param options map of optional parameters to configure the AppExplorer
     * @return object which represents a remote run on AppThwack
     */
    private AppThwackRun scheduleTestRun(AppThwackProject project,
                                        AppThwackDevicePool pool,
                                        String type,
                                        String name,
                                        AppThwackFile app,
                                        AppThwackFile tests,
                                        HashMap<String, String> options) {

        try {
            if (type.equalsIgnoreCase(AppThwackExtension.JUNIT_TYPE)) {
                return project.scheduleJUnitRun(app, tests, name, pool, extension.getTestFilter());
            }
            else if (type.equalsIgnoreCase(AppThwackExtension.CALABASH_TYPE)) {
                return project.scheduleCalabashRun(app, tests, name, pool, extension.getCalabashTags());
            }
            else {
                return project.scheduleAppExplorerRun(app, name, pool, options);
            }
        }
        catch(AppThwackException e) {
            logger.error(e, "[AppThwack] Failed to schedule run '%s' of type '%s'.", name, type);
            return null;
        }
    }

    /**
     * Uploads newly built apk to AppThwack or returns null on error.
     * @param apk File object of the apk to upload.
     * @return Object representing a remote file stored on AppThwack
     */
    private AppThwackFile uploadFile(File apk) {
        try {
            return api.uploadFile(apk);
        }
        catch(AppThwackException e) {
            logger.error(e, "[AppThwack] Failed to upload file '%s'.", apk.getName());
            return null;
        }
    }
}