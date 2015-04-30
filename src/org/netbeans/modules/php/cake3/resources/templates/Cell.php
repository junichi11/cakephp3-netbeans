<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

<#if namespace?? && namespace != "">
namespace ${namespace};

</#if>
use Cake\View\Cell;

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends Cell
{
    public function display()
    {
        
    }
}
