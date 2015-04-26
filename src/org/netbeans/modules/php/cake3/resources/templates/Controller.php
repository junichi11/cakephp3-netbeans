<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

<#if namespace?? && namespace != "">
namespace ${namespace};

</#if>
/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends AppController
{
    
}
