#  Chess Game Analyzer

> Progetto OOP - Analisi delle partite da chess.com tramite API

## 📌 Overview

Questo progetto è stato sviluppato come parte dell'esame di **Programmazione Orientata agli Oggetti (OOP)**. L'obiettivo principale è scaricare e analizzare le partite giocate da un utente su [chess.com](https://www.chess.com/ ) utilizzando le loro API pubbliche.

Il programma effettua una richiesta HTTP all'API di chess.com, recupera gli archivi mensili delle partite dell'utente e li elabora per fornire informazioni statistiche e analitiche sulle sue performance.

---

## 🧩 Funzionalità Implementate (Step 1 & Step 2)

- Chiamata API a:  
  `https://api.chess.com/pub/player/ {username}/games/archives`
- Parsing della risposta JSON tramite libreria **Gson**
- Scaricamento delle partite mensili da ogni URL ricevuto
- Estrazione corretta del vincitore (`white`, `black`, `draw`) dal campo `result` di `white` e `black`
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

### 🔍 Cos'è il Parsing (con Gson)

Il **parsing** di un JSON è il processo con cui si **converte una stringa JSON in un oggetto Java**.

Con la libreria [Gson](https://github.com/google/gson), è possibile fare questa operazione in modo semplice:

#### 📥 Da JSON a Oggetto Java (parsing)

```java
import com.google.gson.Gson;

String json = "{\"nome\":\"Luca\", \"eta\":25}";
Gson gson = new Gson();

Persona persona = gson.fromJson(json, Persona.class);
```

> In questo esempio, la stringa JSON viene "parsata" e trasformata in un oggetto `Persona`.

#### 📤 Da Oggetto Java a JSON (serializzazione)

```java
Persona p = new Persona();
p.nome = "Anna";
p.eta = 30;

String json = gson.toJson(p);
```

> La conversione inversa si chiama **serializzazione**: un oggetto Java viene trasformato in una stringa JSON.



## 📁 Struttura del Progetto
```
chess-analyzer/
├── core/                 # logica principale
│   ├── Main.java
│   └── GameAnalyzer.java
├── api/                  # gestione API
│   ├── ChessComApiClient.java
│   └── PlayerGameResponse.java
├── model/
|   └──Game # singola partita
└── WORK IN PROGRESS...
```

---
### Differenze tra UCI, SAN, FEN e PGN negli Scacchi
## Panoramica dei Formati

Questi quattro formati servono scopi diversi nell'ecosistema degli scacchi:

| Formato | Tipo         | Principale Utilizzo                    | Esempio                                                                                           |
|---------|--------------|----------------------------------------|---------------------------------------------------------------------------------------------------|
| UCI	    | Protocollo   | Comunicazione con motori scacchistici  | position startpos moves e2e4 (da-a)                                                               |
| SAN	    | Notazione    | Mosse leggibili per umani	             | Nf3, e4, O-O (singola mossa)                                                                      |
| FEN     | 	Descrizione | 	Rappresentazione posizioni specifiche | rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1 (situazione generale della scacchiera) |
| PGN     | 	Archivio    | 	Memorizzazione partite complete	      | [Event "Championship"] 1.e4 e5 (elenco mosse fornito da chess.com)                                |
## UCI (Universal Chess Interface)
# Cos'è: 
Protocollo standard per comunicare con motori scacchistici come Stockfish.
# Caratteristiche:
- Formato macchina-leggibile
- Mosse in formato "da-a" (e2e4, g1f3)
- Comandi testuali semplici:
```
position startpos moves e2e4 e7e5
go depth 18
```
# Vantaggi:
- 🚀 Ottimizzato per prestazioni
- 💻 Supporto nativo in Stockfish
- 🔧 Permette controllo granulare (tempo, profondità, varianti)
## SAN (Standard Algebraic Notation)
# Cos'è:
Notazione standard per mosse umano-leggibili.
# Regole:
- Pezzi: K (Re), Q (Regina), R (Torre), B (Alfiere), N (Cavallo)
- Pedoni: solo casella destinazione (e4)
- Catture: x (Bxe5)
- Scacchi: +, matto: #
- Arrocco: O-O (corto), O-O-O (lungo)
# Esempi:
- e4 (pedone)
- Nf3 (cavallo)
- Qxe7+ (regina cattura in e7 con scacco)
## FEN (Forsyth-Edwards Notation)
# Cos'è:
Stringa compatta che descrive una posizione specifica.
# Struttura:
```
rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKB1R b KQkq - 0 2
```
# Descrizione
- Pezzi: 8 gruppi separati da / (dalla riga 8 alla 1)
- Turno: w (bianco) o b (nero)
- Arrocco: KQkq (diritti rimanenti) o -
- En passant: casella (e3) o -
- Contatore mosse: dall'ultima cattura/mossa pedone
- Numero mossa
## PGN (Portable Game Notation)
# Cos'è:
Formato completo per archiviare partite.
# Componenti:
- Intestazione: 
  - Metadati
  ```
  [Tag "Valore"] 
  [Event "F/S Return Match"]
  [Site "Belgrade, Serbia"]
  [White "Fischer, Robert J."]
  ```
- Mosse: In SAN con numerazione
  ```
  1. e4 e5 2. Nf3 Nc6 3. Bb5 a6
  ```
---