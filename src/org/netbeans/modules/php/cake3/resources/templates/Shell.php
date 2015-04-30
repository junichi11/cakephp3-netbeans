<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

<#if namespace?? && namespace != "">
namespace ${namespace};

</#if>
use Cake\Console\Shell;

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends Shell
{
    public function main()
    {
        
    }
}
