/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The SF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.sling.hc.sling.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.request.ResponseUtil;
import org.apache.sling.hc.api.EvaluationResult;

/** Renders a List of EvaluationResult in HTML */
public class HtmlResultRendererImpl implements SlingHealthCheckServlet.Renderer {

    public String getExtension() {
        return "html";
    }
    
    public String getContentType() {
        return "text/html";
    }
    
    public void render(List<EvaluationResult> results, Writer output) throws IOException {
        final PrintWriter pw = new PrintWriter(output);
        final WebConsoleHelper c = new WebConsoleHelper(pw);
        pw.println("<table class='content healthcheck' cellpadding='0' cellspacing='0' width='100%'>");
        for(EvaluationResult r : results) {
            c.titleHtml(r.getRule().toString(), null);
            
            if(!r.getRule().getTags().isEmpty()) {
                dataRow(c, "Tags", ResponseUtil.escapeXml(r.getRule().getTags().toString()));
            }
            
            if(!r.getRule().getInfo().isEmpty()) {
                final StringBuilder sb = new StringBuilder();
                for(Map.Entry<String, Object> e : r.getRule().getInfo().entrySet()) {
                    sb.append(ResponseUtil.escapeXml(e.getKey()))
                    .append(":")
                    .append(ResponseUtil.escapeXml(e.getValue().toString()))
                    .append("<br/>");
                }
                dataRow(c, "Info", sb.toString());
            }
            
            if(r.anythingToReport()) {
                final StringBuilder sb = new StringBuilder();
                for(EvaluationResult.LogMessage msg : r.getLogMessages()) {
                    sb.append("<div class='log").append(msg.getLevel().toString()).append("'>");
                    sb.append(msg.getLevel().toString())
                    .append(" ")
                    .append(ResponseUtil.escapeXml(msg.getMessage()))
                    .append("</div>");
                }
                dataRow(c, "Log", sb.toString());
            } else {
                dataRow(c, "Log", "<span class='nothingToReport'>Nothing to report</a>");
            }
        }
        pw.println("</table>");
    }
    
    private void dataRow(WebConsoleHelper c, String label, String content) {
        c.tr();
        c.tdLabel(ResponseUtil.escapeXml(label));
        c.tdContent();
        c.writer().println(content);
        c.closeTd();
        c.closeTr();
    }
}
