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

import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.ImageStream;
import io.fabric8.utils.cxf.JsonHelper;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 */
public class GitHelpersTest {
    @Test
    public void testExtractGitUrlFromOnlyRemote() throws Exception {
        assertExtractGitUrl("git@github.com:fabric8io/example-camel-cdi.git", "[core]\n" +
                "\trepositoryformatversion = 0\n" +
                "\tfilemode = true\n" +
                "\tbare = false\n" +
                "\tlogallrefupdates = true\n" +
                "\tignorecase = true\n" +
                "\tprecomposeunicode = true\n" +
                "[remote \"origin\"]\n" +
                "\turl = git@github.com:fabric8io/example-camel-cdi.git\n" +
                "\tfetch = +refs/heads/*:refs/remotes/origin/*\n" +
                "[branch \"master\"]\n" +
                "\tremote = origin\n" +
                "\tmerge = refs/heads/master");
    }

    @Test
    public void testExtractGitUrlFromOrigin() throws Exception {
        assertExtractGitUrl("git@github.com:jstrachan/fabric8.git", "[core]\n" +
                "        repositoryformatversion = 0\n" +
                "        filemode = true\n" +
                "        bare = false\n" +
                "        logallrefupdates = true\n" +
                "        ignorecase = true\n" +
                "        precomposeunicode = true\n" +
                "[remote \"upstream\"]\n" +
                "        url = git@github.com:fabric8io/fabric8.git\n" +
                "        fetch = +refs/heads/*:refs/remotes/upstream/*\n" +
                "[remote \"origin\"]\n" +
                "        url = git@github.com:jstrachan/fabric8.git\n" +
                "        fetch = +refs/heads/*:refs/remotes/origin/*\n");
    }

    @Test
    public void testExtractGitUrlFromFirstWhenNoOrigin() throws Exception {
        assertExtractGitUrl("git@github.com:fabric8io/fabric8.git", "[core]\n" +
                "        repositoryformatversion = 0\n" +
                "        filemode = true\n" +
                "        bare = false\n" +
                "        logallrefupdates = true\n" +
                "        ignorecase = true\n" +
                "        precomposeunicode = true\n" +
                "[remote \"upstream\"]\n" +
                "        url = git@github.com:fabric8io/fabric8.git\n" +
                "        fetch = +refs/heads/*:refs/remotes/upstream/*\n" +
                "[remote \"cheese\"]\n" +
                "        url = git@github.com:jstrachan/fabric8.git\n" +
                "        fetch = +refs/heads/*:refs/remotes/origin/*\n");
    }

    @Test
    public void testExtractGitUrlWhenNoRemote() throws Exception {
        assertExtractGitUrl(null, "[core]\n" +
                "        repositoryformatversion = 0\n" +
                "        filemode = true\n" +
                "        bare = false\n" +
                "        logallrefupdates = true\n" +
                "        ignorecase = true\n" +
                "        precomposeunicode = true\n");
    }


    public static void assertExtractGitUrl(String expectedUrl, String gitConfigText) {
        String actual = GitHelpers.extractGitUrl(gitConfigText);
        assertEquals("Expected git url from config: " + gitConfigText, expectedUrl, actual);
    }


}
