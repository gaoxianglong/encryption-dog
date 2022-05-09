<div align=center><img src="https://github.com/gaoxianglong/encryption-dog/blob/master/resources/logo.png"/></div>

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) ![License](https://img.shields.io/badge/build-passing-brightgreen.svg) ![License](https://img.shields.io/badge/version-1.5.6--SNAPSHOT-blue)
> Encryption program with high performance, high security and rich functionsm<br/>
> Supports binding the same physical device for file encryption and decryption<br/>

## Use of EncryptionDog
### install
```Shell
git clone https://github.com/gaoxianglong/encryption-dog.git
mvn package
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.6.jar'
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
	version: 1.5.6-SNAPSHOT

Usage: encrypt-dog [-cdehoV] -k [-k]... [-n=<name>] -s=<source file>
                   [-t=<storage path>]
  -c, --compress          Compression is not enabled by default,Turning on
                            compression will increase execution time.
  -d, --delete            The source file is not deleted after the default
                            operation.
  -e, --encrypt           The default is decryption mode.
  -h, --help              Show this help message and exit.
  -k, --secret-key        Both encrypt and decrypt require the same secret key
  -n, --set-name=<name>   Set the name of the target file.
  -o, --only-local        Encryption and decryption operations can only be
                            performed on the same physical device.
  -s, --source-file=<source file>
                          Target files that need to be encrypt and decrypt,
                            Wildcards are supported.
  -t, --target-path=<storage path>
                          Storage path after operation,The default is stored in
                            the user home directory.
  -V, --version           Print version information and exit.
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
When using batch mode, the source file path must use single quotation marks.
# encrypt
$ dog -es '/Users/jiushu/Desktop/*.mp4' -t /Users/jiushu/Desktop -k
# decrypt
$ dog -s '/Users/jiushu/Desktop/*.dog' -t /Users/jiushu/Desktop -k
```
### delete source file
```shell
# command -d, --delete
# encrypt
$ dog -des /Users/jiushu/Desktop/a.mp4 -k
# decrypt
$ dog -ds /Users/jiushu/Desktop/a.mp4.dog -k
```
### set target file name
The default is the same as the source file name.
```shell
# command -n, --set-name=<name>
# encrypt
$ dog -des /Users/jiushu/Desktop/a.mp4 -n b -k
# decrypt
$ dog -ds /Users/jiushu/Desktop/b.mp4.dog -n c -k
```
### binding the same physical device
Files encrypted on computer a can only be decrypted on computer a.
```shell
# command -o, --only-local
# encrypt
$ dog -deos /Users/jiushu/Desktop/a.mp4 -k
# decrypt
$ dog -ds /Users/jiushu/Desktop/a.mp4 -k
```
### the file is compressed after encryption
This operation is more time-consuming.
```shell
# command -c, --compress
# encrypt
$ dog -ces /Users/jiushu/Desktop/a.mp4 -k
```
### store secret-key
This operation is unsafe. Please use it with caution.
```shell
# command
alias dog = 'java -Ddog-store=true -Xms512m -Xmx512m -Xmn128m -jar dog-1.5.6.jar'
# store style(secret-key to base64)
[dog-file]\:a.b.dog,[source]\:a.b,[source-size]\:0.02MB,[target]\:a.b.dog,[target-size]\:0.05MB=MTIzNDU2bmloYW96YXE\=
```