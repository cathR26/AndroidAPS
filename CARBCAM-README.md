# AAPS V4 + 10BE CarbCam Integration

Dieser Fork ist der offizielle **AAPS V4 master** (nightscout/AndroidAPS)
mit einem einzigen Patch obendrauf: die
**10BE CarbCam-App darf den Bolus-Wizard mit prefill oeffnen**.

Alles andere ist upstream unveraendert. Kein Signature-Bypass, kein
Expiration-Bypass, nichts.

## Zwei Wege zu nutzen

### A) Einfach: diesen Fork forken (1 Klick)

1. Oben rechts auf **Fork** klicken
2. In deinem neuen Fork: GitHub Actions -> "Build APK" ausloesen (oder warten
   bis BrowserBuild automatisch laeuft)
3. APK herunterladen und installieren

Der default-Branch dieses Forks ist `carbcam-master`, dein Fork uebernimmt den.
BrowserBuild baut davon. Fertig.

### B) Fuer die die ihren eigenen Fork von nightscout/AndroidAPS schon haben

Fertige "Compare across forks"-URL: trage deinen GitHub-Benutzernamen ein und
oeffne die URL im Browser. Daraus wird ein Pull Request von diesem Fork
zu deinem Fork — ein Klick auf "Create PR" + "Merge PR" und der CarbCam-Patch
ist in deinem Fork.

```
https://github.com/<DEIN-GITHUB-USER>/AndroidAPS/compare/master...zehnBE:AndroidAPS:carbcam-master?expand=1
```

(Ersetze `<DEIN-GITHUB-USER>` mit deinem Namen.)

## Wird dieser Fork aktuell gehalten?

Ja. Ein GitHub Actions Workflow rebased diesen Branch jeden Montag auf den
neuesten `nightscout/AndroidAPS master`. Wenn upstream einen Konflikt
verursacht (z.B. ComposeMainActivity umgebaut), faelt der Sync rot und wird
manuell nachgezogen.

## Sicherheits-Design

- **Whitelist**: nur `de.be10.carbcam` darf den Wizard extern oeffnen
- **Range-Check**: Carbs 1..80 g, Notes max 200 Zeichen
- **Keine automatische Insulin-Abgabe**: der Patch oeffnet nur den
  AAPS-eigenen Wizard-Dialog. User bestaetigt wie immer selbst.

## Was der Patch macht

Drei Aenderungen:

1. **NEU**: `app/src/main/kotlin/app/aaps/activities/WizardLaunchActivity.kt`
   — Trampoline-Activity mit Whitelist + Range-Check, forwarded zur
   ComposeMainActivity.
2. **AndroidManifest.xml**: registriert die Trampoline-Activity mit
   Intent-Filter `info.nightscout.androidaps.action.OPEN_BOLUS_WIZARD`.
3. **ComposeMainActivity.kt**: liest die Carbs/Notes aus dem Intent und
   navigiert ueber die V4-eigene `AppRoute.WizardDialog.createRoute(carbs,
   notes)` in den Wizard.

## Kontakt

- CarbCam: https://carbcam.app
- Support: support@ns.10be.de
- Blog: https://ns.10be.de/blog/2026/06/01/carbcam-kohlenhydrate-direkt-in-den-aaps-bolus-wizard-uebernehmen
