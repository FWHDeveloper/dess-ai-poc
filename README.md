# DESS.AI : assistant de maintenance avec sources

Prototype d'assistant qui répond à une question technique **à partir de manuels ingérés**, en **citant sa source** (manuel, section, page). Il refuse de répondre quand l'information n'existe pas.

**Démo vidéo (2 min) :** [lien vidéo]

## Le problème

Un technicien à bord d'un navire doit retrouver une valeur précise (par exemple un couple de serrage) dans un manuel de plusieurs centaines de pages. Une mauvaise valeur peut endommager le matériel. Une réponse sans source ne peut pas être vérifiée.

## Ce que fait le prototype

- **Ingestion** d'extraits de manuels avec métadonnées : référence du manuel, section, page.
- **Recherche** de l'extrait pertinent.
- **Réponse** rédigée par une IA locale (Llama2 via Ollama), uniquement à partir de l'extrait retrouvé.
- **Traçabilité** : chaque réponse renvoie ses sources.
- **Garde-fou** : si le contexte ne contient pas la réponse, l'application ne devine pas.
- **Mode toujours signalé** dans la réponse.

## Stack

| Élément | Choix |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot, Spring AI (1.0.0-M5) |
| LLM | Llama2 via Ollama (local) |
| Stockage vectoriel | **Simulé en mémoire** (filtre par mots-clés) |
| Tests / API | Postman |

## Prérequis

- JDK 17
- Maven
- [Ollama](https://ollama.com) installé et lancé
- Modèle : `ollama pull llama2`

## Lancer le projet

```bash
ollama pull llama2
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

## API

### 1. Ingérer un extrait

`POST /api/v1/dess-ai/ingest`

```json
{
  "docRef": "MAN-PROP-4412",
  "section": "§3.2",
  "page": 117,
  "content": "[texte de l'extrait, par exemple : le couple de serrage requis pour la pompe de cale est de 210 N·m, clé OUT-77]"
}
```

### 2. Poser une question

`POST /api/v1/dess-ai/diagnose`

```json
{ "question": "Quel est le couple de serrage requis pour la pompe de cale ?" }
```

Réponse :

```json
{
  "answer": "Le couple de serrage requis est de 210 N·m.",
  "sources": [
    { "docRef": "MAN-PROP-4412", "section": "§3.2", "page": 117 }
  ],
  "mode": "NORMAL"
}
```

## Modes de réponse

| Mode | Signification |
|---|---|
| `NORMAL` | Un extrait pertinent a été trouvé et la réponse en découle |
| `NO_CONTEXT` | Aucun extrait pertinent : l'application refuse de répondre |
| `NO_ANSWER` | Extrait trouvé, mais il ne contient pas la réponse (si implémenté) |
| `DEGRADED` | La recherche est indisponible : réponse sûre, sans source |

Principe : **une absence de réponse vaut mieux qu'une réponse inventée.**

## Limites assumées

- Le **stockage est simulé en mémoire** (filtre par mots-clés) : les données disparaissent au redémarrage. Il faut ingérer avant de questionner, sans redémarrer.
- La pertinence dépend des mots communs entre la question et le texte ingéré.
- Llama2 (petit modèle local) est parfois approximatif en français.
- Pas d'authentification, pas de gestion des versions de manuels.
- Pas d'évaluation chiffrée de la qualité des réponses.

## Prochaines étapes

1. Remplacer le stockage simulé par **pgvector** (PostgreSQL) avec de vrais embeddings (`mxbai-embed-large`).
2. Mesurer la qualité sur un jeu de questions (réponses correctes, refus à raison).
3. Gérer les **versions de manuels** et leur validité.
4. Ajouter l'authentification et un journal d'audit des réponses.
5. Tester un modèle local plus performant en français.

## Auteure

**Fatma Weld Hamood**, ingénieure études et développement Java / Full Stack
[LinkedIn](https://linkedin.com/in/fatma-weldhamood) · fatma.whamood@gmail.com

