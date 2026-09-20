// Backend / KI-Verarbeitungsschicht fuer die LernKI-App.
//
// Aufgabe dieses Servers (siehe Abschnitt 12 der Anforderungen):
// - Frontend (Android-App), Backend (dieser Server) und KI-Verarbeitung
//   sauber voneinander trennen.
// - Der API-Key des KI-Anbieters bleibt serverseitig und wird NIE an die
//   App ausgeliefert.
// - Das eingesetzte KI-Modell kann hier zentral ausgetauscht werden
//   (aktuell: Anthropic Claude), ohne dass die App angepasst werden muss.

require('dotenv').config();
const express = require('express');
const cors = require('cors');
const Anthropic = require('@anthropic-ai/sdk');
const {
  summarizePrompt,
  mathSolvePrompt,
  classifyImagePrompt,
  learningMaterialPrompt,
  chatSystemPrompt
} = require('./prompts');

const PORT = process.env.PORT || 3000;
const MODEL = process.env.ANTHROPIC_MODEL || 'claude-sonnet-4-6';

const anthropic = new Anthropic({
  apiKey: process.env.ANTHROPIC_API_KEY
});

const app = express();
app.use(cors());
app.use(express.json({ limit: '30mb' })); // Bilder als Base64 koennen gross sein

// --- Hilfsfunktionen -------------------------------------------------

function imagesToContentBlocks(images = []) {
  return images.map((img) => ({
    type: 'image',
    source: {
      type: 'base64',
      media_type: img.mimeType || 'image/jpeg',
      data: img.base64
    }
  }));
}

function extractText(message) {
  return message.content
    .filter((block) => block.type === 'text')
    .map((block) => block.text)
    .join('\n');
}

function parseJsonResponse(rawText) {
  // Die KI wird angewiesen, nur JSON zu liefern; zur Sicherheit werden
  // eventuelle Markdown-Codeblock-Zaeune entfernt.
  const cleaned = rawText.replace(/```json/gi, '').replace(/```/g, '').trim();
  return JSON.parse(cleaned);
}

async function callClaudeJson({ systemPrompt, text, images, userPreamble }) {
  const content = [];
  if (userPreamble) content.push({ type: 'text', text: userPreamble });
  if (text) content.push({ type: 'text', text });
  content.push(...imagesToContentBlocks(images));

  if (content.length === 0) {
    content.push({ type: 'text', text: '(kein Inhalt uebergeben)' });
  }

  const response = await anthropic.messages.create({
    model: MODEL,
    max_tokens: 4000,
    system: systemPrompt,
    messages: [{ role: 'user', content }]
  });

  return parseJsonResponse(extractText(response));
}

function handleError(res, err, context) {
  console.error(`[${context}] Fehler:`, err);
  res.status(500).json({
    error: `Die KI-Verarbeitung ist fehlgeschlagen (${context}).`,
    detail: err.message
  });
}

// --- Endpunkte ---------------------------------------------------------

// 1. Zusammenfassen (Abschnitt 1)
app.post('/api/summarize', async (req, res) => {
  try {
    const { text, images, style } = req.body;
    const result = await callClaudeJson({
      systemPrompt: summarizePrompt(style || 'NORMAL'),
      text,
      images
    });
    res.json(result);
  } catch (err) {
    handleError(res, err, 'summarize');
  }
});

// 2. + 3. Mathe loesen, inkl. mehrerer Aufgaben pro Bild (Abschnitt 2 + 3)
app.post('/api/solve-math', async (req, res) => {
  try {
    const { text, images } = req.body;
    const result = await callClaudeJson({
      systemPrompt: mathSolvePrompt(),
      text,
      images
    });
    res.json(result);
  } catch (err) {
    handleError(res, err, 'solve-math');
  }
});

// 6. Bildanalyse: erkennt Art des Bildinhalts (Abschnitt 6 + 10)
app.post('/api/classify-image', async (req, res) => {
  try {
    const { image } = req.body;
    const result = await callClaudeJson({
      systemPrompt: classifyImagePrompt(),
      images: [image]
    });
    res.json(result);
  } catch (err) {
    handleError(res, err, 'classify-image');
  }
});

// 4. Lernmodus: Zusammenfassung, Lernzettel, Karteikarten, Quiz, ... (Abschnitt 4)
app.post('/api/learning-material', async (req, res) => {
  try {
    const { text, images, materialType } = req.body;
    const result = await callClaudeJson({
      systemPrompt: learningMaterialPrompt(materialType || 'LERNZETTEL'),
      text,
      images
    });
    res.json(result);
  } catch (err) {
    handleError(res, err, 'learning-material');
  }
});

// 5. KI-Chat mit optionalem Bild-/Dokumentkontext (Abschnitt 5)
app.post('/api/chat', async (req, res) => {
  try {
    const { messages, contextText, contextImages } = req.body;

    const historyBlocks = (messages || []).map((m, index) => {
      // Beim ersten Nutzer-Turn den Kontext (Bild/Text) mit anhaengen.
      if (index === 0 && m.role === 'user') {
        const content = [];
        if (contextText) content.push({ type: 'text', text: `Kontextmaterial:\n${contextText}` });
        content.push(...imagesToContentBlocks(contextImages));
        content.push({ type: 'text', text: m.content });
        return { role: 'user', content };
      }
      return { role: m.role, content: m.content };
    });

    const response = await anthropic.messages.create({
      model: MODEL,
      max_tokens: 1500,
      system: chatSystemPrompt(Boolean(contextText || (contextImages && contextImages.length))),
      messages: historyBlocks
    });

    res.json({ reply: extractText(response) });
  } catch (err) {
    handleError(res, err, 'chat');
  }
});

app.get('/health', (_req, res) => res.json({ status: 'ok', model: MODEL }));

app.listen(PORT, () => {
  console.log(`LernKI-Backend laeuft auf Port ${PORT} (Modell: ${MODEL})`);
});
