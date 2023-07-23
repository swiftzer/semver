package net.swiftzer.semver

internal actual val PreReleasePattern: Regex
    get() =
        Regex("""(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*""")
internal actual val BuildMetadataPattern: Regex
    get() =
        Regex("""[0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*""")

@Suppress("MaxLineLength")
internal actual val FullPattern: Regex
    get() =
        Regex("""(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?""")
