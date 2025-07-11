#  Chess Game Analyzer

> Progetto OOP - Analisi delle partite da chess.com tramite API

## 📌 Overview

Questo progetto è stato sviluppato come parte dell'esame di **Programmazione Orientata agli Oggetti (OOP)**. L'obiettivo principale è scaricare e analizzare le partite giocate da un utente su [chess.com](https://www.chess.com/ ) utilizzando le loro API pubbliche.

Il programma effettua una richiesta HTTP all'API di chess.com, recupera gli archivi mensili delle partite dell'utente e li elabora per fornire informazioni statistiche e analitiche sulle sue performance.

---

## 🧩 Funzionalità Implementate (Step 1)

- Chiamata API a:  
  `https://api.chess.com/pub/player/ {username}/games/archives`
- Parsing della risposta JSON tramite libreria **Gson**
- Creazione della classe `PlayerGameResponse` per rappresentare i dati ricevuti
- Separazione pulita tra logica di rete, modello dati e punto di ingresso (`Main.java`)

### Output atteso:
```
Archivi trovati per nomeUtente:
https://api.chess.com/pub/player/nomeUtente/games/2024/03
https://api.chess.com/pub/player/nomeUtente/games/2024/02
```

---

## 🛠️ Tecnologie Utilizzate

| Componente       | Tecnologia / Libreria         |
|------------------|-------------------------------|
| Linguaggio       | Java (17)                     |
| Build System     | Maven                         |
| Parsing JSON     | Gson                          |
| API              | chess.com Public API          |
| Ambiente IDE     | IntelliJ IDEA                 |

---

## 📁 Struttura del Progetto
chess-analyzer/
├── core/                 # logica principale
│   ├── Main.java
│   └── GameAnalyzer.java
├── api/                  # gestione API
│   ├── ChessComApiClient.java
│   └── PlayerGameResponse.java
├── WORK IN PROGRESS...

