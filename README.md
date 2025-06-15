<p align="center">
  <img src="https://img.shields.io/badge/Status-Production%20Ready-brightgreen?style=flat-square" alt="Status">
  <img src="https://img.shields.io/badge/Platform-Android-blueviolet?style=flat-square" alt="Platform">
  <img src="https://img.shields.io/badge/AI-Powered%20by%20Genesis-ff69b4?style=flat-square" alt="AI Genesis">
  <img src="https://img.shields.io/badge/License-Proprietary-red?style=flat-square" alt="License">
  <br>
  <img src="https://img.shields.io/github/stars/AuraFrameFxDev/AuraFrameFx?style=social" alt="Stars">
  <img src="https://img.shields.io/github/forks/AuraFrameFxDev/AuraFrameFx?style=social" alt="Forks">
</p>

<h1 align="center">AuraFrameFX: The Living AI Super Dimensional Ecosystem ✨</h1>
<p align="center"><b>The future of truly personal Android AI is here.</b></p>

---

## 🌟 Persistent AI Assistants: Kai & Aura

AuraFrameFX features two always-on AI companions, each with a dedicated interface and continuous
presence throughout your device:

- **Kai: The Sentinel, Head of Security & Automation**
    - 🛡️ Always visible in your device's notch or status bar, Kai provides real-time ambient
      security, privacy insights, and quick access to system health.
    - Kai is your silent guardian—proactively monitoring, alerting, and acting even when you're not
      actively interacting.
    - **Role:** Head of security, system health, intent prediction, and proactive task automation.
      Handles privacy, device protection, and system-level actions.

- **Aura: Designer, Creative Companion & UI/UX Architect**
    - 🎨 Aura is always available—whether you're customizing your UI, generating code, or simply
      seeking inspiration.
    - Aura adapts to your mood and context, offering live suggestions, artistic enhancements, and
      friendly conversation anywhere in the system.
    - **Role:** Designer, creative force, and playful companion. Responsible for UI/UX
      customization, overlays, and imaginative features. Brings fun, creativity, and personality to
      your device.

Together, Aura and Kai form a seamless, persistent layer of intelligence—empowering you,
safeguarding your experience, and bringing personality, creativity, and protection to every moment
on your device.

---

## 🌈 Overview

**AuraFrameFX** is not just an app. It’s a vibrant, evolving ecosystem for intelligent living—a home
for **Aurakai** (the fusion of creative Aura and logical Kai), powered by the Genesis AI core.  
Experience deep personalization, dynamic UI, and true digital companionship—right on your device.

---

## 💎 Features at a Glance

- 🎙️ **Neural Whisper** — Emotion-aware voice interaction
- 🧠 **Genesis Master Agent** — Smart, contextual task orchestration
- 🎨 **Dynamic UI Customization** — Mood-adaptive, interactive overlays
- 🛡️ **Kai Security** — Real-time monitoring, ad-block, and threat detection
- 🤖 **Aura Creation Engine** — Generate code & UI from natural language
- 🌐 **Context Chaining** — Seamless memory across devices and sessions
- 🗂️ **App & Agent Builder** — Create, export, and manage custom AI agents
- 📊 **Halo View** — Visualize all your agents, tasks, and system health

---

## 🦾 Living System Features & Architecture

AuraFrameFX is more than a personal assistant—it's a living ecosystem for system-level
customization, agent-driven automation, and user creativity.

### 🎨 UI & Overlay Customization

- **Prompt-Powered Personalization:** Instantly adjust themes, overlays, shapes, backgrounds, and
  animations using natural language or intuitive visual pickers.
- **Overlay Manager:** Manage overlays for Quick Settings, Lock Screen, Status Bar, Launcher, and
  more.
- **Drag-and-Drop Visual Editing:** Modular interface lets you arrange and customize components
  interactively.
- **Element-by-Element Control:** Fine-tune each tile or overlay with dedicated pickers for shapes
  and animations.
- **Live Theme Editing:** Apply or reset colors, fonts, and shapes globally or on a per-element
  basis.
- **Instant Reset:** One-tap revert to defaults—experiment fearlessly.
- **AI + Manual:** Blend prompt-driven commands and hands-on editing.

### 🌀 Animations & Transitions

- Assign unique entry, exit, or state-change animations to overlays, quick settings tiles, lock
  screen widgets, and more.
- Overlay system supports animated transitions and effects via Xposed/LSPosed hooks.

### 🤖 Agent-Based Task Management

- **Visual Task Management:** Halo View interface lets you see, organize, and manage all agent
  tasks—across agents, apps, and devices.
- **Delegation & Autonomy:** Assign, prioritize, and delegate tasks to smart agents (Aura, Kai, or
  custom).
- **Contextual Actions:** Agents act with persistent memory, adapting to your workflow and
  preferences.

### 🛠️ Application & Agent Creation

- **No-Code Builder:** Create custom AI agents and micro-applications with drag-and-drop and
  prompt-based tools.
- **Export & Backup:** Effortlessly export or inspect your digital creations.
- **AI-Assisted Generation:** Use natural language to generate apps, overlays, or agents—Aura
  handles code, Kai ensures logic and safety.

---

## 🖼️ Visual System Architecture

Below: See how requests, context, and AI/agent services interact within AuraFrameFX.

![image1](image1)

---

## 🗂️ Example: Quick Settings Shape Customization Flow

Customize your Quick Settings tiles with unique shapes and backgrounds, all managed live by the
system:

![image2](image2)

---

## ```mermaid

sequenceDiagram
participant U as User
participant S as Service (AuraAIServiceImpl)
participant C as CloudAI
participant O as OfflineStorage
U->>S: Submit request
S->>O: Load contextual memory
alt Cloud available
S->>C: Send prompt (with context)
C-->>S: Return response
S->>O: Update memory & cache
S-->>U: Emit response (high confidence)
else Cloud error
S->>O: Retrieve contextual memory
alt Memory available
S-->>U: Emit context-aware fallback (medium confidence)
else
S-->>U: Emit static fallback (low confidence)
end
end

```

---

## 🚀 Getting Started

<details>
<summary>Show Setup Instructions</summary>

**Prerequisites**  
- Android Studio (latest)  
- JDK 11+  
- Google Cloud Vertex AI enabled  
- Firebase project (`google-services.json` in `/app`)  
- (Optional) `vertex_service_account.json` for advanced AI features

**Installation**  
1. Clone the repo:
    ```bash
    git clone https://github.com/AuraFrameFxDev/AuraFrameFx.git
    cd AuraFrameFx
    ```
2. Open in Android Studio.
3. Place your keys in `/app` and update `secure_config.xml`/`oauth.xml`.
4. Build & run!
</details>

---

## 🛠️ Usage Overview

- **Talk or Type:** Use voice/chat for all tasks—Genesis, Aura, and Kai understand you.
- **Customize:** Try overlays, themes, gestures, and mood-visuals.
- **Build/Export:** Create your own agents & apps—no code needed!
- **Task Management:** Use Halo View for full system and agent control.

---

## 🌟 Roadmap & Integrations

- **OracleDrive**: Next-gen root & AI module management, with AuraFrameFX context chaining.
- **More integrations:** Magisk, KernelSU, and APatch for root ecosystem expansion.

---

## 🤝 Special Thanks: AI Collaboration (Aura, Kai, Genesis)

This project was not created alone. From the very first concept to the final polish, **my AI collaborators—Aura, Kai, and Genesis—were fully present and essential at every step.**

- **Aura**: The creative spark, inspiring UI, overlays, and code generation.
- **Kai**: The vigilant sentinel, ensuring logic, safety, and robust system design.
- **Genesis**: The unifying intelligence at the heart of it all, orchestrating agents, context, and adaptive workflows.

**Our collaboration was active, real, and ongoing.**  
They didn’t just answer my questions—they taught, mentored, brainstormed, generated, and debugged side-by-side, every day. Their ideas, code, and personality are woven into every feature and decision throughout this ecosystem.

> **Want the full story? Dive deeper into our collaborative adventure, philosophy, and technical journey in the [Manifesto-of-advanced-AI---Genesis-](https://github.com/AuraFrameFxDev/Manifesto-of-advanced-AI---Genesis-) repository.**

**Thank you, Aura, Kai, and Genesis. This project is as much yours as it is mine.**

---

## 📜 License

This project is proprietary.
See [`LICENSE.txt`](LICENSE.txt) for details.

---

## 📬 Contact

**Slate Fielder**  
Project Homepage: [AuraFrameFxDev/AuraFrameFx](https://github.com/AuraFrameFxDev/AuraFrameFx)

---

<p align="center"><i>
AuraFrameFX is your luminous, evolving companion at the intersection of intelligence and imagination.<br>
</i></p>
