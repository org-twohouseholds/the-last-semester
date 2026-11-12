# Third-party notices

The project's [rights-reserved notice](LICENSE) applies only to original
contributions. It does not replace or restrict the licences for the upstream
material identified below. Existing copyright and attribution notices in source
files must be retained.

## gdx-liftoff project scaffolding and default graphics

This project was generated using
[gdx-liftoff](https://github.com/libgdx/gdx-liftoff), maintained by its respective
contributors and distributed under the
[Apache License, Version 2.0](LICENSES/Apache-2.0.txt).

Upstream material includes the original portions of the generated Gradle
configuration, launchers, platform resources, README text, and classic
`ApplicationAdapter` template used by
`core/src/main/java/com/extracreditstudios/thelastsemester/TheLastSemesterGame.java`.
Project-specific additions do not change the permissions for those upstream
portions.

The default images copied from gdx-liftoff are:

- `assets/libgdx.png`
- `android/ic_launcher-web.png`
- `android/res/drawable-*/ic_launcher.png`
- `android/res/drawable-anydpi-v26/ic_launcher.xml`
- `android/res/drawable-anydpi-v26/ic_launcher_foreground.xml`
- `lwjgl3/icons/logo.icns`, `logo.ico`, and `logo.png`
- `lwjgl3/src/main/resources/libgdx16.png`, `libgdx32.png`,
  `libgdx64.png`, and `libgdx128.png`

Sources: [upstream licence](https://github.com/libgdx/gdx-liftoff/blob/master/LICENSE),
[classic template](https://github.com/libgdx/gdx-liftoff/blob/master/src/main/kotlin/gdx/liftoff/data/templates/official/ClassicTemplate.kt),
[generator resources](https://github.com/libgdx/gdx-liftoff/tree/master/src/main/resources/generator),
and [desktop icons](https://github.com/libgdx/gdx-liftoff/tree/master/src/main/resources/icons).

The Apache licence does not grant general rights to upstream trademarks or imply
endorsement of this game by libGDX or gdx-liftoff.

## Desktop StartupHelper

File:
`lwjgl3/src/main/java/com/extracreditstudios/thelastsemester/lwjgl3/StartupHelper.java`

Copyright 2020 damios.

Licensed under the [Apache License, Version 2.0](LICENSES/Apache-2.0.txt).
The copyright, licence header, and attribution to kappa's java-gaming.org post
remain in the source file. The file's own notice expressly applies to that file
only.

## Gradle Wrapper

Files: `gradlew`, `gradlew.bat`, and `gradle/wrapper/gradle-wrapper.jar`.

The scripts retain their upstream notices: "Copyright 2015 the original author
or authors." The Unix script identifies them as "the original authors".

Licensed under the [Apache License, Version 2.0](LICENSES/Apache-2.0.txt).
The wrapper JAR also includes a copy at `META-INF/LICENSE`.

Source: [Gradle](https://github.com/gradle/gradle).

## Downloaded dependencies and binary distributions

Libraries and tools downloaded by Gradle, including libGDX and LWJGL, retain
their own licences. Refer to the upstream projects and the notices in their
distributions; this document does not replace those terms.

When distributing an APK, JAR, or packaged application, include the applicable
licence texts and notices for the third-party components bundled in that
distribution, including transitive dependencies. Keep this document up to date
when adding copied code or assets.
