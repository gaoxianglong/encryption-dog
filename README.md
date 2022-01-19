# encryption-dog
### install
```Shell
git clone https://github.com/gaoxianglong/encryption-dog.git
mvn package
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.3.jar'
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
        version: 1.3-SNAPSHOT

Usage: encrypt-dog [-dehV] -k [-k]... -s=<source file> [-t=<storage path>]
  -d, --delete       The source file is not deleted after the default operation
  -e, --encrypt      The default is decryption mode
  -h, --help         Show this help message and exit.
  -k, --secret-key   Both encrypt and decrypt require the same secret key
  -s, --source-file=<source file>
                     Target files that need to be encrypt and decrypt
  -t, --target-path=<storage path>
                     Storage path after operation,The default is stored in the
                       temporary directory
  -V, --version      Print version information and exit.
Copyright(c) 2021-2031

# encrypt&remove source
$ dog -des /Users/jiushu/Desktop/test.txt -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key):
Enter the secret-key again: 
Please wait...
[Estimated completion time]:0.11s
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
Encrypt	success
[Time-consuming]:0.08ms,[Before size]:0.01MB,[After size]:0.03MB
[Target path]:/Users/jiushu/Desktop/test.txt.dog

# decrypt
$ dog -s /Users/jiushu/Desktop/test.txt.dog -t /Users/jiushu/Desktop -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key):
Please wait...
[Estimated completion time]:0.10s
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
Decrypt	success
[Time-consuming]:0.07ms,[Before size]:0.03MB,[After size]:0.01MB
[Target path]:/Users/jiushu/Desktop/test.txt
```
### file structure
|  file        | extension name |  type          |   magic number |   location   |    amount     |     ascii    |
|  :-:         | :-:            |  :-:           |   :-:          |    :-:       |     :-:       |      :-:     |
| DOG FORMAT   |     .dog       |  u4/32bit      |   0x19890225   |    header    |       1       |      ...     |