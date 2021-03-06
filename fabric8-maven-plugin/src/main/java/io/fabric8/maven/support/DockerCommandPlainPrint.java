/*
 * Copyright 2005-2015 Red Hat, Inc.                                    
 *                                                                      
 * Red Hat licenses this file to you under the Apache License, version  
 * 2.0 (the "License"); you may not use this file except in compliance  
 * with the License.  You may obtain a copy of the License at           
 *                                                                      
 *    http://www.apache.org/licenses/LICENSE-2.0                        
 *                                                                      
 * Unless required by applicable law or agreed to in writing, software  
 * distributed under the License is distributed on an "AS IS" BASIS,    
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      
 * implied.  See the License for the specific language governing        
 * permissions and limitations under the License.
 */
package io.fabric8.maven.support;

import java.util.Map;

public class DockerCommandPlainPrint {

    private static final String DOCKER_PREFIX_LOG_OUTPUT = "docker run -dP ";
    
    private StringBuilder dockerPlainTextCommand;

	public DockerCommandPlainPrint(StringBuilder dockerPlainTextCommand) {
		super();
		this.dockerPlainTextCommand = dockerPlainTextCommand;
		this.dockerPlainTextCommand.append(DOCKER_PREFIX_LOG_OUTPUT);
	}

	public StringBuilder getDockerPlainTextCommand() {
		return dockerPlainTextCommand;
	}

	public void setDockerPlainTextCommand(StringBuilder dockerPlainTextCommand) {
		this.dockerPlainTextCommand = dockerPlainTextCommand;
	}
	
	public void appendParameters(Map<String, String> data, String flag) {
		for (Map.Entry<String, String> entry : data.entrySet())
		{
			dockerPlainTextCommand.append(flag);
		    dockerPlainTextCommand.append(" ");
		    dockerPlainTextCommand.append(entry.getKey());
		    dockerPlainTextCommand.append("=");
		    dockerPlainTextCommand.append(entry.getValue());
		    dockerPlainTextCommand.append(" ");
		}
	}
	
	public void appendImageName(String imageName) {
		dockerPlainTextCommand.append(imageName);
	}
    
}
