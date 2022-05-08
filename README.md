<div align=center><img src="https://github.com/gaoxianglong/encryption-dog/blob/master/resources/logo.png"/></div>

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) ![License](https://img.shields.io/badge/build-passing-brightgreen.svg) ![License](https://img.shields.io/badge/version-0.1--SNAPSHOT-blue.svg)
> Encryption program with high performance, high security and rich functionsm<br/>
> Supports binding the same physical device for file encryption and decryption<br/>

## Use of EncryptionDog
### install
```Shell
git clone https://github.com/gaoxianglong/encryption-dog.git
mvn package
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.5.jar'
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
        version: 1.5.5-SNAPSHOT

Usage: encrypt-dog [-cdehoV] -k [-k]... -s=<source file> [-t=<storage path>]
  -c, --compress     Compression is not enabled by default,Turning on
                       compression will increase execution time.
  -d, --delete       The source file is not deleted after the default operation.
  -e, --encrypt      The default is decryption mode.
  -h, --help         Show this help message and exit.
  -k, --secret-key   Both encrypt and decrypt require the same secret key
  -o, --only-local   Encryption and decryption operations can only be performed
                       on the same physical device.
  -s, --source-file=<source file>
                     Target files that need to be encrypt and decrypt,Wildcards
                       are supported.
  -t, --target-path=<storage path>
                     Storage path after operation,The default is stored in the
                       user home directory.
  -V, --version      Print version information and exit.
Copyright(c) 2021-2031

# encrypt&remove&only-local source
$ dog -deos /Users/jiushu/Desktop/a.mp4 -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key): 
Enter the secret-key again:
Please wait...

[Encrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4
[Estimated time]:00:00:06
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Encrypt result]:success
[Time consuming]:00:00:05,[Before size]:107.22MB,[After size]:142.96MB
[Target path]:/Users/jiushu/Desktop//a.mp4.dog

>>> Operation complete <<<
[Total time]:00:00:05
[Results]:total files:1,successes:1,failures:0

# decrypt
$ dog -s /Users/jiushu/Desktop/a.mp4.dog -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key): 
Please wait...

[Decrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4.dog
[Estimated time]:00:00:06
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Decrypt result]:success
[Time consuming]:00:00:05,[Before size]:142.96MB,[After size]:107.22MB
[Target path]:/Users/jiushu/Desktop//a.mp4

>>> Operation complete <<<
[Total time]:00:00:05
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
### compress
```shell
# command
$ dog -ces /Users/jiushu/Desktop/a.mp4 -k
```
### store secret-key
```shell
# command
alias dog = 'java -Ddog-store=true -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.5.jar'
# store style(secret-key to base64)
[dog-file]\:a.b.dog,[source]\:a.b,[source-size]\:0.02MB,[target]\:a.b.dog,[target-size]\:0.05MB=MTIzNDU2bmloYW96YXE\=
```
