<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
  <#if section="header">
    ${msg("redirect.to-jans")}
  <#elseif section="form">
    ${kcSanitize(msg("redirect.too-long-click-here"))?no_esc}
  </#if>
</@layout.registrationLayout>