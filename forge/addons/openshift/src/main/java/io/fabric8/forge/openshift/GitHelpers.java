/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.forge.openshift;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class GitHelpers {
    public static String extractGitUrl(String configText) {
        String remote = null;
        String lastUrl = null;
        String firstUrl = null;
        BufferedReader reader = new BufferedReader(new StringReader(configText));
        Map<String, String> remoteUrls = new HashMap<>();
        while (true) {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                // ignore should never happen!
            }
            if (line == null) {
                break;
            }
            if (line.startsWith("[remote ")) {
                String[] parts = line.split("\"");
                if (parts.length > 1) {
                    remote = parts[1];
                }
            } else if (line.startsWith("[")) {
                remote = null;
            } else if (remote != null && line.length() > 0 && Character.isWhitespace(line.charAt(0))) {
                String trimmed = line.trim();
                if (trimmed.startsWith("url ")) {
                    String[] parts = trimmed.split("=", 2);
                    if (parts.length > 1) {
                        lastUrl = parts[1].trim();
                        if (firstUrl == null) {
                            firstUrl = lastUrl;
                        }
                        remoteUrls.put(remote, lastUrl);
                    }
                }

            }
        }
        String answer = null;
        if (remoteUrls.size() == 1) {
            return lastUrl;
        } else if (remoteUrls.size() > 1) {
            answer = remoteUrls.get("origin");
            if (answer == null) {
                answer = firstUrl;
            }
        }
        return answer;
    }
}
