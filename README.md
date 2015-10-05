# Settings
Configuration File API

GCF stands for "Group Configuration File", i.e. the 
data is saved in Groups. This class offers access to those 
groups which in turn offer access to the underlying configuration 
data.<br>
The basic syntax is as follow:<br>

```
--------- GCF START------------
    global_var = "some var"

    [Constants]
        int     = 10
        float   = 12.48
        boolean = true
        string  = "some string"
        var     = ${global_var}
        [Sub]
            nr = 1000
        [/Sub]
    [/Constants]
--------- GCF END -------------
```

The configuration data is saved as key/value pairs within a group.
The key names can be composed of letters, numbers and underscores, but
must start with a letter. The value can be integers, floats 
(with a dot as the decimal separator), booleans, strings
(enclosed in double quotes) or a reference to a global variable.<br>
The groups are defined using square brackets inbetween which 
the groupname is defined. The rules for the groupname are the 
same as for the keys.<br>
A group has a start and end definition (similar to XML). If 
a group is not closed correctly, either by mispelling of the 
group name, or missing the "group end tag", an exception is thrown,
during parsing.<br>
The content of a group can be key/values or other (sub)groups.<br><br>

To get access to a specific group one can use the <code>Settings</code>
instance as follows:

```java
final Settings set = new Settings(someFile);
final Group constGroup = set.getGroup("/Constants/");
```

With the group instance one can get access to the configuration data
and/or subgroups.

```java
final String str = constGroup.readString("string");
final Group subGroup = constGroup.getGroup("Sub/");
```

Notice how the path to retrieve a sub group is relative.<br><br>
Furthermore the Group class allows one to add new keys or 
change the values of existing keys, as well as adding and 
deleting subgroups.<br>
For any of these actions to take effect one has to explicity
save these changes

```java
// create this key/value inside the Constants group
constGroup.addKey("some_new_key",9999.99);

// save the changes
set.save();
```
 