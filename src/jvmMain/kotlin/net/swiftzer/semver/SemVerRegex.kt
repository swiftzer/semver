package net.swiftzer.semver

internal actual val preReleasePattern: Regex get() = Regex("""(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*""")
internal actual val buildMetadataPattern: Regex get() = Regex("""[0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*""")
internal actual val fullPattern: Regex get() = Regex("""(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?""")
