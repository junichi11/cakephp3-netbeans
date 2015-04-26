<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

<#if namespace?? && namespace != "">
namespace ${namespace};

</#if>
use Cake\ORM\Behavior;

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends Behavior
{
    
}
