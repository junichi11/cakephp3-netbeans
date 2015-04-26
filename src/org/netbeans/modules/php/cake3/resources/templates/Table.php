<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

<#if namespace?? && namespace != "">
namespace ${namespace};

</#if>
use Cake\ORM\Table;

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends Table
{
    
}
