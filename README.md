# [Katalon Studio] A Test Suite Collection forks multiple sub-processes

This is a small [Katalon Studio](https://katalon.com/katalon-studio/) project for demonstration purpose.
You can download the zip of the project from the [Releases](page), unzip it, open it
with your local Katalon Studio.

This project was developed using Katalon Studio v8.3.0. This is not version-dependent. It will work on any version of KS.


## Problem to solve

In the Katalon User forum, there is a topic ["Stopping executing test collection in KRE"](https://forum.katalon.com/t/stopping-executing-test-collection-in-kre/64677). The original poster wrote:

>Tests are run manually via command line using KRE. Tests are in a test collection which comprises of around 100s of test suites which each test suite having around 200-300 data variations in data files. The tests would run more than 24 hours and sometimes there is a need to abort the remaining of the tests.
>How do we cleanly stop the KRE tests? Observations by killing it via Ctrl-c would result to locked up Reports directory and sometimes tests are still running (by looking at resource monitor).

I presume that he did Ctrl-c to kill the OS process in which Katalon Runtime Engine are running.

It seems to me that he expects (wants) everything is gracefully terminated by killing that process by Ctrl-c, but things does not go like this. Why?

## Why? 

Katalon Runtime Engine (or similarly, Katalon Studio) runs in a single OS process.
When you run a Test Suite Collection which comprises 2 Test Suites,
the KRE process **forks 2 subprocesses** each of which Test Suite runs.
I am going to present you a demostration of this fact later.

OS will regard these 3 processes are independent.
Killing one of these 3 process does NOT automatically terminate other sub-processes.
Therefore, as the original post wrote, it is quite likely that we observer

>"by killing it via Ctrl-c would result to locked up Reports directory and sometimes tests are still running" 

Sending Ctrl-c to the parent process will not be a solution to the problem.

## Demonstration

In this demo project, we have

- a Test Case named "TC1". See the source [here](https://github.com/kazurayam/KsTestSuiteCollectionComprisesWithMultipleOSProcesses/blob/master/Scripts/TC1/Script1651370219154.groovy). At startup, it emits a message "I am busy now". It sleeps for a few seconds (10 to 20 secs). Then it emit a message saying Goodby.
- a Test Suite named "TS1" which just calls "TC1".
- a Test Suite Collection named "TSC". It will invoke 2 instances of "TS1" parallely.
- a bash script named "./watch_ps.sh". It will repeat executing a shell command with 5 seconds interval. It will execute "ps -A" command to list the running OS processes; it will filter lines for the lines with a string "Katalon"; it will shorten the filtered lines (just to make it easier to see).

I will run this set of code on macOS with bash commandline.

I will run the `./watch_ps.sh` first. Then I will run the Test suite collection `TSC`.

When I did it, I saw the following out come in the console.

```
$ ./watch_ps.sh
 5991 ??         4:05.17 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:05.38 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:08.07 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:12.97 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:14.14 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:15.87 /Applications/Katalon Studio.app/Contents/MacOS/katalon
 7418 ??         0:08.62 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy

 5991 ??         4:18.52 /Applications/Katalon Studio.app/Contents/MacOS/katalon
 7418 ??         0:09.66 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy
 7424 ??         0:05.53 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy

 5991 ??         4:19.68 /Applications/Katalon Studio.app/Contents/MacOS/katalon
 7418 ??         0:10.22 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy
 7424 ??         0:09.44 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy

 5991 ??         4:20.97 /Applications/Katalon Studio.app/Contents/MacOS/katalon
 7424 ??         0:09.45 /Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy

 5991 ??         4:21.86 /Applications/Katalon Studio.app/Contents/MacOS/katalon

 5991 ??         4:22.54 /Applications/Katalon Studio.app/Contents/MacOS/katalon
```

Here you can find 2 types of OS processes.

One is a process where `/Applications/Katalon Studio.app/Contents/MacOS/katalon` is being executed. This is the process where Katalon Studio is running.

Another is a process where `/Applications/Katalon Studio.app/Contents/Eclipse/jre/Contents/Home/bin/java -Dgroovy` is being executed. This is a process where Test Suite is running.

In the console, you can find 1 process of Katalon Studio plus 2 processes of Test Suites are bein executed parallely.

If I have a Test Suite Collection of 8 parallelism, then I will see 8 sub-processes in the `ps` command output.

### Is a Test Suite Collection multi-threaded? --- Not at all.

Sometimes, people in the Katalon Forum talk about "parallel execution". Some people seems to have a misunderstanding.
They seem to think that a Test Suite Collection runs a set of multiple "threads" each of which Test Suites runs.
You should not confuse "multi threads" and "multi processes".
These 2 models plays completely different.

## Conclusion

When you run a Test Suite Collection with 2 Test Suites contained, you will have 3 OS processes running independently.
Killing one of them by Ctrl+C will not automatically terminate this set of processes gracefully.
