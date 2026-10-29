<!--
  This README is the PUBLIC FACE of YOUR project.
  Replace the example content with that of your application.
  Do NOT put AI instructions here — that lives in AGENTS.md.
-->

<!-- Replace X and Title -->
# Assignment `Projeto Final`

**Course:**  Mobile Application Development (DAM)

**Student Number:** `49845`

**Student Name:** `Gonçalo Barbosa`

**Student Email:** `a49845@alunos.isel.pt`

**Student class:** `62D`

**Student GitHub:** `https://github.com/Gon1507`

**Date:** `15/06/2026`


# Zero Waste

ZeroWaste is a smart pantry management assistant that helps individuals save money and reduce food waste through intuitive inventory tracking and proactive and gamified insights.

![status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
![android](https://img.shields.io/badge/Android-Kotlin%20%7C%20Compose-3DDC84)

## Demo

| Screen 1 | Screen 2 | Screen 3 |
|---|---|---|
| <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/5a84fb7a-3f2b-4869-a261-b69bcc425c95" /> | <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/d1f9e13c-8e01-464c-a844-ca42eeb7d0ce" /> | <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/e72a1bd7-d2c6-4c4b-9355-660ec0205f3b" /> |
| Screen 4 | Screen 5 | Screen 6 |
| <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/ab35ea0f-52af-4d53-88b2-d7cee8bb3eb0" /> | <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/93dba1ee-45fe-4ad3-a280-2168ac2df950" /> | <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/54dc7caa-a761-434a-b1a2-b960be6ed9f9" /> |
|---|---|---|
| <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/5d4537cd-1143-4dd8-ad9f-80ad67329bbe" /> | <img width="920" height="2048" alt="image" src="https://github.com/user-attachments/assets/beb2cf0b-f37b-4de0-8149-428d9c79865b" /> | - |

## Features

- [x] Screen 1 — Login Screen
- [x] Screen 2 — Registration Screen
- [x] Screen 3 — Main Screen
- [x] Screen 4 — Add Product
- [x] Screen 5 — Inventory
- [x] Screen 6 — AI Recipes
- [x] Screen 7 — Statistics
- [x] Screen 8 — User's Profile
- [x] State sharing between users
- [x] AI integration - Generate Daily Tips and Recipes
- [x] Multimedia (image / audio / video)
- [x] Freemium model (free usage + simulated paid subscription)
- [x] Offline mode

## Stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel · MVVM ·
StateFlow · Repository Pattern · Hilt/Koin · Retrofit/Ktor · Coil · Room · DataStore.

## How to run

```bash
git clone <repo>
cd <repo>
# Configure secrets (see docs/10_security_and_permissions.md)
cp local.properties.example local.properties   # and fill in the keys
./gradlew assembleDebug
```

Open in Android Studio (recommended version in `docs/06_architecture.md`) and run on an
emulator/device with the indicated minimum API.

## Architecture

Short summary + diagram. Full detail in [`docs/06_architecture.md`](docs/06_architecture.md).

## Documentation

All the engineering design is in [`docs/`](docs/). Decisions in [`docs/adr/`](docs/adr/).

## Team

| Name | No. | Role |
|---|---|---|
| Gonçalo Barbosa | A49845 | Full Developer |

## AI Usage

This project was developed with the assistance of AI tools according to the rules in
[`AGENTS.md`](AGENTS.md). Usage log in
[`docs/14_ai_usage_log.md`](docs/14_ai_usage_log.md) and reflection in
[`docs/15_postmortem.md`](docs/15_postmortem.md).

## License

See [`LICENSE`](LICENSE).
