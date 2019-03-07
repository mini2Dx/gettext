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

    @Override
    void apply(Project project) {
        NamedDomainObjectContainer<GetTextSource> sourceContainer = project.container(ServerEnvironment.class, new NamedDomainObjectFactory<GetTextSource>() {
            public GetTextSource create(String name) {
                return new GetTextSource(name);
            }
        });

        project.getExtensions().add("gettext", sourceContainer);

        sourceContainer.all(new Action<GetTextSource>() {
            @Override
            void execute(GetTextSource getTextSource) {
                final String sourceType = getTextSource.getName()l
                final String capitalizedSourceType = sourceType.substring(0, 1).toUpperCase() + sourceType.substring(1);
                final String taskName = "generatePot" + capitalizedSourceType;

                project.getTasks().register(taskName, GeneratePotTask.class, new Action<GeneratePotTask>() {
                    public void execute(GeneratePotTask task) {
                        task.source = getTextSource;
                    }
                });
            }
        });
    }
}
