/*******************************************************************************
 * Copyright 2019 Thomas Cashman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mini2Dx.gettext.plugin

import org.gradle.api.*
import org.mini2Dx.gettext.plugin.task.GeneratePotTask

class GetTextPlugin implements Plugin<Project> {
    final List<String> potTaskNames = new ArrayList<String>();

    @Override
    void apply(Project project) {
        NamedDomainObjectContainer<GetTextSource> sourceContainer = project.container(GetTextSource.class, new NamedDomainObjectFactory<GetTextSource>() {
            public GetTextSource create(String name) {
                return new GetTextSource(name);
            }
        });

        project.getExtensions().add("gettext", sourceContainer);

        sourceContainer.all(new Action<GetTextSource>() {
            @Override
            void execute(GetTextSource getTextSource) {
                final String sourceType = getTextSource.getName();
                final String capitalizedSourceType = sourceType.substring(0, 1).toUpperCase() + sourceType.substring(1);
                final String taskName = "generatePot" + capitalizedSourceType;
                potTaskNames.add(taskName);

                project.getTasks().register(taskName, GeneratePotTask.class, new Action<GeneratePotTask>() {
                    public void execute(GeneratePotTask task) {
                        task.srcDir = getTextSource.srcDir;
                        task.include = getTextSource.include;
                        task.exclude = getTextSource.exclude;
                        task.excludes = getTextSource.excludes;
                        task.commentFormat = getTextSource.commentFormat;
                        task.forceExtractFormat = getTextSource.forceExtractFormat;
                        task.commentFormat = getTextSource.commentFormat;

                        File outputDirectory;
                        if(getTextSource.outputPath == null) {
                            outputDirectory = new File(project.getBuildDir(), 'gettext');
                        } else if(getTextSource.outputPath.startsWith(".")) {
                            outputDirectory = new File(project.getProjectDir(), getTextSource.outputPath);
                        } else {
                            outputDirectory = new File(getTextSource.outputPath);
                            if(!outputDirectory.isAbsolute()) {
                                outputDirectory = new File(project.getProjectDir(), getTextSource.outputPath);
                            }
                        }
                        task.outputFile = new File(outputDirectory, getTextSource.outputFilename);
                    }
                });
            }
        });
        project.getTasks().register("generatePots").configure {
            setDependsOn(potTaskNames)
        };
    }
}
