shimakaze-pr
============

Yet another SOCKS5 proxy for KanColle to prpr Shimakaze happily,
which supports connections multi-backend relay and proxy-level retry.

## Motivation 

 * \_(:з」∠)\_ .rprp zekamaShi
 * Name `pr` means Proxy Retry too :)
 * As error `neko` is big boss of the enemies :(
 * Try netty 4.1/5.x proxy handler.
 * Try kinds of IoC.

## Feature

 * Nothing

WIP :P

## Usage

```
mvn package
java [-Dport={thisProxyPort} -Dsocks5Host={upstreamProxyHost} -Dsocks5Port={upstreamProxyPort}] -jar target/shimakaze-pr-1.0-jar-with-dependencies.jar
```

Defaults:
 * thisProxyPort = 1080
 * upstreamProxyHost = null
 * upstreamProxyPort = 1080

## License

DWYW
