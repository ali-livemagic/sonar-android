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

import com.android.tools.lint.checks.BuiltinIssueRegistry;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.Issue.OutputFormat;
import com.android.tools.lint.detector.api.Severity;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This class tests the AndroidLintRuleRepository class
 *
 * @author Florian Roncari
 */
public class AndroidLintRuleRepositoryTest {

  @Test
  public void createRulesTest() {
    List<Rule> rules;
    RuleRepository rulerep = new AndroidLintRuleRepository(new XMLRuleParser());
    rules = rulerep.createRules();

    assertThat(rules.size()).isEqualTo(158);
  }

  @Test
  public void verifyRuleDescriptorsAndL10N() throws Exception {
    IssueRegistry registry = new BuiltinIssueRegistry();
    StringBuilder rulesxml = new StringBuilder();
    StringBuilder androidProperties = new StringBuilder();
    Map<String, String> rulesDescriptions = new HashMap<String, String>();
    rulesxml.append("<rules>").append("\n");
    for (Issue issue : registry.getIssues()) {
      rulesxml.append("  <rule>").append("\n");
      rulesxml.append("    <key>").append(issue.getId()).append("</key>").append("\n");
      rulesxml.append("    <configKey>").append(issue.getId()).append("</configKey>").append("\n");
      rulesxml.append("    <priority>").append(toSonarSeverity(issue.getDefaultSeverity())).append("</priority>").append("\n");
      rulesxml.append("  </rule>").append("\n");

      androidProperties.append("rule.android-lint.").append(issue.getId()).append(".name=").append(issue.getBriefDescription(OutputFormat.TEXT)).append("\n");

      StringBuilder description = new StringBuilder();
      description.append("<p>").append(issue.getDescription(OutputFormat.HTML)).append("</p>\n\n<p>")
          .append(issue.getExplanation(OutputFormat.HTML)).append("</p>\n");
      if (!issue.getMoreInfo().isEmpty()) {
        description.append("\n<ul>\n<li>").append(Joiner.on("</li>\n<li>").join(issue.getMoreInfo())).append("</li></ul>\n");
      }
      rulesDescriptions.put(issue.getId(), description.toString());
    }
    rulesxml.append("</rules>").append("\n");

    File rules = new File("target/rules.xml");
    FileUtils.write(rules, rulesxml.toString(), Charsets.UTF_8.name());
    assertFileEquals("/org/sonar/plugins/android/lint/rules.xml", rulesxml.toString());

    File props = new File("target/android.properties");
    FileUtils.write(props, androidProperties.toString(), Charsets.UTF_8.name());
    assertFileEquals("/org/sonar/l10n/android.properties", androidProperties.toString());

    for (Entry<String, String> entry : rulesDescriptions.entrySet()) {
      File desc = new File("target/rules/" + entry.getKey() + ".html");
      FileUtils.write(desc, entry.getValue(), Charsets.UTF_8.name());
    }
    for (Entry<String, String> entry : rulesDescriptions.entrySet()) {
      assertFileEquals("/org/sonar/l10n/android/rules/android-lint/" + entry.getKey() + ".html", entry.getValue());
    }
  }

  private void assertFileEquals(String file, String content) throws IOException {
    assertThat(IOUtils.toString(this.getClass().getResourceAsStream(file), Charsets.UTF_8.name()).replaceAll("\r\n", "\n")).isEqualTo(content);
  }

  /**
   * Convert Android Lint {@link Severity} to Sonar {@link RulePriority}
   * <p/>
   * Default mapping:
   * |------------------------|
   * | Android     | Sonar    |
   * |-------------|----------|
   * | N/A         | BLOCKER  |
   * | FATAL       | CRITICAL |
   * | ERROR       | MAJOR    |
   * | WARNING     | MINOR    |
   * | INFORMATION | INFO     |
   * | IGNORE      | N/A      |
   * |------------------------|
   *
   * @param severityLint Android Lint Severity
   * @return Sonar Severity
   */
  public static RulePriority toSonarSeverity(Severity severityLint) {
    switch (severityLint) {
      case FATAL:
        return RulePriority.CRITICAL;
      case ERROR:
        return RulePriority.MAJOR;
      case WARNING:
        return RulePriority.MINOR;
      case INFORMATIONAL:
        return RulePriority.INFO;
      default:
        return RulePriority.INFO;
    }
  }
}