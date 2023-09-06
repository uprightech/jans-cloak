<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
  <#if section="header">
    ${msg("jans.redirect.to-jans")}
  <#elseif section="form">
    <form id="kc-jans-login-form" class="${properties.kcFormClass!}" action="${jansLoginUrl}" method="post">

       <div class="${properties.kcFormGroupClass!}">
         <div id="kc-form-buttoms" class="${properties.kcFormButtonsClass!}">
          <div class="${properties.kcFormButtonsWrapperClass!}">
            <input class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" 
                   name="login" id="kc-login" type="submit" value="${msg('jans.redirect.too-long-click-here')}"/>
          </div>
         </div>
       </div>

    </form>
  </#if>
</@layout.registrationLayout>