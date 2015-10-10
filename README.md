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

    # Some multiline
    # comment
    [Constants]
        int     = 10 # some inline comment
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

The configuration data is saved as key/value pairs within groups.<br>
The key names can be composed of letters, numbers, underscores,
dots and dashes, but must start with a letter.<br>
The values can be integers, floats (with a dot as the decimal separator), 
booleans, strings (enclosed in double quotes) or a reference to a 
global variable.<br>
A group is defined using square brackets inbetween which 
the groupname is defined.<br>
It has an opening and closing statement (similar to XML). If 
a group is not closed properly, either by mispelling of the 
group name, or missing the "end group" syntax, an exception is thrown,
during parsing.<br>
The rules for the groupname are the same as for the keys, with the
exception that groupnames can start with numbers.<br>
The content of a group can consist of key/values and/or other (sub)groups.<br>
Comments are started with a '#' symbol.<br><br>
 
To get access to a specific group one uses the <code>Settings</code>
 class as follows:

```java
final Settings set = new Settings(someFile);
final Group constGroup = set.getGroup("/Constants/");
```

With the Settings instance reference one can get access to the configuration 
data and/or the subgroups.

```java
// Get a reference to the "Constants" group
final Group constGroup = constGroup.getGroup("/Constants/");

// Read a value for the key "string"
final String str = constGroup.readString("string");

// Get a reference to the subgroup "Sub"
final Group subGroup = constGroup.getGroup("Sub/");
```

A group is retrieved using its full path, so as if it was in a 
(Unix) filesystem. A subgroup on the other hand is retrieved using
the relative path, starting at the parent.<br>
To access the value of a key, one passes the key name as a string
to the respective type method, e.g. <code>readInt</code> 
to retrieve an integer.<br><br>

Besides allowing the retrieval of subgroups, the Group class allows 
the adding of new keys or changing the values of existing keys, as well as 
adding and deleting subgroups.<br>
For any of these actions to take effect though, one has to explicitly
save these changes

```java
// create this key/value inside the Constants group
constGroup.addKey("some_new_key",9999.99);

// save the changes
set.save();
```
