# NetBeans CakePHP3/4 Plugin

This plugin provides support for CakePHP3/4.

## Requirements

- NetBeans 12.0+

## Downloads

- https://github.com/junichi11/cakephp3-netbeans/releases
- https://plugins.netbeans.apache.org/catalogue/?id=27

## How To Install

### Via NetBeans Plugin Portal Update Center

- Tools > Plugins > Available Plugins
- Click the "Check for Newest" button
- Check "CakePHP3/4 Framework"
- Click the "Install" button

### Use downloaded nbms

- Download a nbm (e.g. org-netbeans-modules-php-cake3-0.0.1-dev-201408251540.nbm)
- Tools > Plugins > Downloaded > Add Plugins
- Select the nbm
- Click Install

## How To Update

You can update to the new version by the same way as the install. You don't have to uninstall the old version.

## How To Enable

- Open the project properties dialog (Right-Click your project > properties)
- Frameworks > CakePHP3/4 > Check `Enabled`

## Use Your Custom Directory Structure

If you just installed Cake3/4 via Composer, you don't have to do anything.

- Open the project properties
- Set relative paths from your Source Directory to Path settings

#### Root

Use when your CakePHP app directory exists in your php project as a subdirectory.

e.g. set `app` to Root setting like the following case:
```
source directory
    ├── foo
    ├── bar
    └── app
            ├── README.md
            ├── bin
            ├── composer.json
            ├── composer.lock
            ├── config
            ├── index.php
            ├── logs
            ├── phpunit.xml.dist
            ├── plugins
            ├── src
            ├── tests
            ├── tmp
            ├── vendor
            └── webroot
```

#### The Others

The same as App settings of config/app.php

#### Templates, plugins, locales, e.t.c.

Support them using `.cake` file.

## Features

- Code completion
- Smart Go To
- Run Actions (Run Command)
- Template files(Controller, Table, Helper, e.t.c.)
- Support for a `.cake` file
- Resolve mime-types for a ctp extension and a `.cake` file
- Show a parent directory name of a ctp file in the multi-row tabs(see Tools > Options > Appearance > Document Tabs) e.g. `home.ctp [Pages]`
- Custom nodes

### Code Completion

**Component**
```php
// in your Controller e.g. SomeController or AppController
public $components = ['Foo', 'My.Bar', 'Bar' => ['className' => 'MyBar'] ];

public initialize() {
    $this->loadComponent('Foo');
    $this->laodComponent('Bar', ['className' => 'MyBar']);
}

// in your Controller
$this->[Ctrl+Space]
```

**Helper**
```php
// in your Controller e.g. SomeController or AppController
public $helpers = ['Foo', 'My.Bar', 'Bar' => ['className' => 'MyBar'] ];

// in your template file e.g. index.ctp
$this->[Ctrl+Space]
```

**Table**
```php
// in your Controller e.g. SomeController 
$this->loadModel('Users');

// in your Controller
$this->[Ctrl+Space]
```

Please add `@property` to your table class if you want to use like the following:

```php
// e.g. BookmarksTable
/**
 * @property \App\Model\Table\TagsTable $Tags 
 */
class BookmarksTable extends Table {
}

$this->Bookmarks->Tags->[Ctrl+Space]
```

**Method Parameters**
```php
// e.g. path/to/your/template/index.ctp
// file path completion
$this->Html->css('[Ctrl+Space]');

// constants
$this->Html->docType('[Ctrl+Space]');
```

**NOTE**
Please add a semicolon(<kbd>;</kbd>) if you want to use code completions for parameters. Tips: You can add it like the following: <kbd>Ctrl</kbd>+<kbd>;</kbd>

### Smart Go To

This feature is not enabled by default. If you want to use it, please set the KeyMap to it.
(Tools > Options > Keymap > Search `CakePHP` > CakePHP3/4: Smart Go To > e.g. set <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>J</kbd>) 

Files related to the current editor are shown when you run this action.
e.g. If your current file is a Controller, template, table, entity, testcase, conponent and helper file(s) will be shown.

You can change a list to specific category's one like the following.
(<kbd>Ctrl</kbd> is a Ctrl or Command key)

- Controller(All) : <kbd>Ctrl</kbd> + <kbd>C</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>C</kbd>)
- Component : <kbd>Ctrl</kbd> + <kbd>P</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>P</kbd>)
- Table : <kbd>Ctrl</kbd> + <kbd>M</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>M</kbd>)
- Entity : <kbd>Ctrl</kbd> + <kbd>E</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>E</kbd>)
- Behavior : <kbd>Ctrl</kbd> + <kbd>B</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>B</kbd>)
- Templates : <kbd>Ctrl</kbd> + <kbd>V</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>V</kbd>)
- View Cell : <kbd>Ctrl</kbd> + L (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>L</kbd>)
- Helper : <kbd>Ctrl</kbd> + <kbd>H</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>H</kbd>)
- Fixture : <kbd>Ctrl</kbd> + <kbd>F</kbd> (<kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>F</kbd>)
- TestCase : <kbd>Ctrl</kbd> + <kbd>T</kbd>
- Config : <kbd>Ctrl</kbd> + <kbd>I</kbd> or <kbd>Ctrl</kbd> + <kbd>Shift</kbd> + <kbd>I</kbd>

**NOTE** Core files are not shown (e.g. HtmlHelper, AuthComponent, e.t.c.)

### Support for a .cake file

You can use the [.cake](https://github.com/dotcake/dotcake) if you want to use the specified directories for Controller, Table, Template, e.t.c..
The file format is the following:
```json
{
    "cake": ".\/vendor\/cakephp\/cakephp",
    "build_path": {
        "entities": [
            ".\/src\/Model\/Entity\/"
        ],
        "tables": [
            ".\/src\/Model\/Table\/"
        ],
        "behaviors": [
            ".\/src\/Model\/Behavior\/"
        ],
        "controllers": [
            ".\/src\/Controller\/"
        ],
        "components": [
            ".\/src\/Controller\/Component\/"
        ],
        "templates": [
            ".\/src\/Template\/"
        ],
        "views": [
            ".\/src\/View\/"
        ],
        "helpers": [
            ".\/src\/View\/Helper\/"
        ],
        "consoles": [
            ".\/src\/Console\/"
        ],
        "shells": [
            ".\/src\/Shell\/"
        ],
        "tasks": [
            ".\/src\/Shell\/Task\/"
        ],
        "locales": [
            ".\/src\/Locale\/"
        ],
        "vendors": [
            ".\/vendor\/"
        ],
        "plugins": [
            ".\/plugins\/"
        ]
    }
}
```

**NOTE** It is not available in any categories.

### Custom nodes

You can add some directories(nodes) under the your project tree. Controller, Model, e.t.c. are shown by default.
If you want to hide/show them, please change the options.(Tools > Options > PHP > Frameworks and Tools > CakePHP3/4 > Custom nodes)

## Actions

Right-click a project > CakePHP3/4

- Run Command
- Run server
- Refresh

### Run Command

All commands will be shown as a list in the command dialog. Then you can run a command with some parameters.

### Run server

Just run `cake server`. If you want to set details, please use `Run Configuration` of project properties.

### Refresh

Please run this action after you changed the `.cake` file or you updated the version of CakePHP.
Refresh the version number and category paths.

## Donation

- https://github.com/sponsors/junichi11

## Issues

If you have issues, please submit them to [GitHub Issues](https://github.com/junichi11/cakephp3-netbeans/issues) .
Please don't create PRs soon.

## License

Apache License, Version 2.0
