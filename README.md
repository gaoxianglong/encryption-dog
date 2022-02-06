# encryption-dog
### install
```Shell
git clone https://github.com/gaoxianglong/encryption-dog.git
mvn package
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.jar'
```
### use
```Shell
$ dog -h
Welcome to 
   ____                       __  _           ___           
  / __/__  __________ _____  / /_(_)__  ___  / _ \___  ___ _
 / _// _ \/ __/ __/ // / _ \/ __/ / _ \/ _ \/ // / _ \/ _ `/
/___/_//_/\__/_/  \_, / .__/\__/_/\___/_//_/____/\___/\_, / 
                 /___/_/                             /___/  
        version: 1.5-SNAPSHOT

Usage: encrypt-dog [-dehoV] -k [-k]... -s=<source file> [-t=<storage path>]
  -d, --delete       The source file is not deleted after the default operation
  -e, --encrypt      The default is decryption mode
  -h, --help         Show this help message and exit.
  -k, --secret-key   Both encrypt and decrypt require the same secret key
  -o, --only-local   Encryption and decryption operations can only be performed
                       on the same physical device
  -s, --source-file=<source file>
                     Target files that need to be encrypt and decrypt,Wildcards
                       are supported
  -t, --target-path=<storage path>
                     Storage path after operation,The default is stored in the
                       user home directory
  -V, --version      Print version information and exit.
Copyright(c) 2021-2031

# encrypt&remove&only-local source
$ dog -deos /Users/jiushu/Desktop/a.mp4 -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key): 11
Please wait...

[Encrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4
[Estimated completion time]:6.60s
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Encrypt result]:success
[Time-consuming]:5.86s,[Before size]:107.22MB,[After size]:142.96MB
[Target path]:/Users/jiushu/Desktop//a.mp4.dog

>>> Operation complete <<<
[Total time]:5.87s
[Results]:total files:1,successes:1,failures:0

# decrypt
$ dog -s /Users/jiushu/Desktop/a.mp4.dog -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key): 11
Please wait...

[Decrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4.dog
[Estimated completion time]:6.38s
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Decrypt result]:success
[Time-consuming]:5.92s,[Before size]:142.96MB,[After size]:107.22MB
[Target path]:/Users/jiushu/Desktop//a.mp4

>>> Operation complete <<<
[Total time]:5.92s
[Results]:total files:1,successes:1,failures:0
```
### file structure
|  file        | file extension name |  type          |   magic number |   location   |    amount     |     ascii    |
|  :-:         |        :-:          |  :-:           |   :-:          |    :-:       |     :-:       |      :-:     |
| DOG FORMAT   |        .dog         |  u4/32bit      |   0x19890225   |    header    |       1       |      ...     |
### batch
```shell
# command
$ dog -deos '/Users/jiushu/Desktop/*.mp4' -t /Users/jiushu/Desktop -k
```
### store secret-key
```shell
# command
alias dog = 'java -Ddog-store=true -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.jar'
# store style(secret-key to base64)
[dog-file]\:a.b.dog,[source]\:a.b,[source-size]\:0.02MB,[target]\:a.b.dog,[target-size]\:0.05MB=MTIzNDU2bmloYW96YXE\=
```
