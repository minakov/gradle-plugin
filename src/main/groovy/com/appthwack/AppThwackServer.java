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

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.android.builder.testing.api.TestServer;
import com.android.utils.ILogger;

import java.io.File;

public class AppThwackServer extends TestServer {

    private final AppThwackExtension extension;
    private final ILogger logger;

    AppThwackServer(@NonNull AppThwackExtension extension,
                    @NonNull ILogger logger) {
        this.extension = extension;
        this.logger = logger;
    }

    @Override
    public String getName() {
        return "appthwack";
    }

    @Override
    public void uploadApks(@NonNull String variantName, @NonNull File testApk, @Nullable File testedApk) {
        System.out.println(String.format(
                "APPTHWACK: Variant(%s), Uploading APKs\n\t%s\n\t%s",
                variantName,
                testApk.getAbsolutePath(),
                testedApk != null ? testedApk.getAbsolutePath() : "<none>"));
    }

    @Override
    public boolean isConfigured() {
        // TODO: detect authentication is actually configured and return false if not.
        return true;
    }
}
