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

Gson gson = new Gson();
return gson.fromJson(response.body(), PlayerGameResponse.class);



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
