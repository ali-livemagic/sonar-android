/*
 * SonarQube Android Plugin
 * Copyright (C) 2013 SonarSource and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.android.lint;

import com.google.common.collect.ImmutableList;
import org.sonar.api.CoreProperties;
import org.sonar.api.SonarPlugin;
import org.sonar.api.Property;

import java.util.List;

@Property( 
  key = AndroidLintConstants.PROJECT_PATH_PROPERTY,
  name = "Android project path",
  description = "Path (relative to the base path) where the Android project resides (i.e. where the AndroidManifest.xml is located).",
  category = CoreProperties.CATEGORY_JAVA,
  project = true,
  global = false
)
public class AndroidLintPlugin extends SonarPlugin {

  @Override
  public List<?> getExtensions() {
    return ImmutableList.of(
        AndroidLintSensor.class,
        AndroidLintRuleRepository.class,
        AndroidLintSonarWay.class,
        AndroidLintExecutor.class);
  }
}
