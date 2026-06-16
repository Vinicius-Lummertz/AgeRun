# 04 — Design System

## 4.1 Princípios de Design

O Age GO adota uma identidade visual esportiva, moderna e motivacional, com foco em clareza de informação e eficiência de uso em contextos de mobilidade (durante treinos ou deslocamentos).

**Princípios:**
1. **Clareza** — Informação acessível em poucos toques
2. **Confiança** — Visual profissional que transmite credibilidade
3. **Energia** — Paleta vibrante que remete ao universo esportivo
4. **Simplicidade** — Interface enxuta, sem poluição visual
5. **Consistência** — Padrões replicáveis em todo o app

---

## 4.2 Paleta de Cores

### Cores Primárias
| Token | Nome | Hex | Uso |
|---|---|---|---|
| `color_primary` | Roxo Profundo | `#3D0066` ou similar escuro | Backgrounds principais, header |
| `color_accent` | Verde Lima | `#AAFF00` ou `#C6FF00` | CTAs, destaques, progresso |
| `color_secondary` | Lilás | `#9B5DE5` | Elementos secundários, tags |

> **Nota:** A identidade visual do briefing usa fundo roxo escuro com detalhes verde-neon. Confirmar hex exatos com o designer antes da implementação.

### Cores Neutras
| Token | Nome | Hex | Uso |
|---|---|---|---|
| `color_background` | Preto suave | `#0D0D0D` | Background geral |
| `color_surface` | Cinza escuro | `#1A1A1A` | Cards, modais |
| `color_surface_variant` | Cinza médio | `#2A2A2A` | Campos de input, dividers |
| `color_on_surface` | Branco | `#FFFFFF` | Textos sobre dark |
| `color_on_surface_muted` | Cinza claro | `#AAAAAA` | Textos secundários, placeholders |

### Cores de Estado
| Token | Nome | Hex | Uso |
|---|---|---|---|
| `color_success` | Verde | `#4CAF50` | Treino concluído, pagamento ok |
| `color_warning` | Amarelo | `#FFC107` | Pagamento pendente, atenção |
| `color_error` | Vermelho | `#F44336` | Erro, inativo, alerta |
| `color_info` | Azul | `#2196F3` | Informações, notificações |

### Gradientes
```
gradient_primary: linear(135deg, #3D0066 → #9B5DE5)
gradient_accent:  linear(90deg, #AAFF00 → #00E5FF)
```

---

## 4.3 Tipografia

### Fonte Principal
- **Família:** `Inter` (Google Fonts — disponível no Android)
- **Fallback:** `Roboto` (fonte padrão Android)

### Escala Tipográfica

| Token | Tamanho | Peso | Line Height | Uso |
|---|---|---|---|---|
| `text_display` | 32sp | Bold (700) | 40sp | Títulos de boas-vindas |
| `text_h1` | 28sp | Bold (700) | 36sp | Títulos de seção principal |
| `text_h2` | 24sp | SemiBold (600) | 32sp | Subtítulos de seção |
| `text_h3` | 20sp | SemiBold (600) | 28sp | Títulos de card |
| `text_h4` | 18sp | Medium (500) | 26sp | Rótulos importantes |
| `text_h5` | 16sp | Medium (500) | 24sp | Subtítulos de card |
| `text_h6` | 14sp | Medium (500) | 20sp | Labels, badges |
| `text_body` | 14sp | Regular (400) | 22sp | Texto corrido |
| `text_caption` | 12sp | Regular (400) | 18sp | Legendas, metadados |
| `text_overline` | 10sp | Medium (500) | 16sp | Rótulos em caps |

### Regras Tipográficas
- Nunca usar tamanhos abaixo de 10sp
- Textos sobre fundos escuros: sempre `#FFFFFF` ou `color_on_surface_muted`
- Evitar uso de itálico fora de citações
- Usar `letter_spacing = 0.5` para textos em caps (overline)

---

## 4.4 Espaçamento e Grid

### Sistema de Espaçamento (múltiplos de 4dp)
| Token | Valor | Uso |
|---|---|---|
| `space_xs` | 4dp | Espaço mínimo entre elementos inline |
| `space_sm` | 8dp | Padding interno de chips, badges |
| `space_md` | 16dp | Padding padrão de cards e telas |
| `space_lg` | 24dp | Separação entre seções |
| `space_xl` | 32dp | Margens de tela em conteúdo maior |
| `space_xxl` | 48dp | Espaçamento entre blocos maiores |

### Grid de Tela
- **Margem horizontal da tela:** 16dp
- **Gutter entre colunas:** 8dp
- **Grid de 4 colunas** (mobile padrão)
- **Área segura bottom (navigation bar):** 80dp reservados para a barra de navegação

---

## 4.5 Elevação e Sombras

```xml
<!-- Nível 1 — Cards planos -->
elevation="2dp"

<!-- Nível 2 — Cards destacados -->
elevation="4dp"

<!-- Nível 3 — Modais e Bottom Sheets -->
elevation="8dp"

<!-- Nível 4 — FAB e elementos flutuantes -->
elevation="12dp"
```

---

## 4.6 Bordas e Cantos

| Token | Valor | Uso |
|---|---|---|
| `corner_xs` | 4dp | Badges, chips pequenos |
| `corner_sm` | 8dp | Inputs, botões pequenos |
| `corner_md` | 12dp | Cards padrão |
| `corner_lg` | 16dp | Cards grandes, bottom sheets |
| `corner_xl` | 24dp | Modais, painéis de perfil |
| `corner_full` | 50% | Avatares, botões circulares |

---

## 4.7 Componentes

### Button (Botão)

**Variantes:**
- `ButtonPrimary` — Fundo accent (verde lima), texto escuro, corners 8dp
- `ButtonSecondary` — Fundo transparente, borda accent, texto accent
- `ButtonGhost` — Sem fundo, sem borda, texto accent
- `ButtonDestructive` — Fundo error (vermelho), texto branco

**Estados:**
- `default` — Cor padrão
- `pressed` — Opacity 80%, scale 0.97
- `disabled` — Opacity 40%, não-clicável
- `loading` — CircularProgressIndicator inline, texto oculto

**Especificações:**
```
height: 48dp
padding_horizontal: 24dp
text: text_h5 / Medium
corner_radius: corner_sm (8dp)
```

### TextField (Campo de Texto)

**Variante padrão:**
- Fundo: `color_surface_variant`
- Borda em repouso: transparente
- Borda ativa (focused): `color_accent` 2dp
- Borda de erro: `color_error` 2dp
- Label flutuante (Material style)
- Corner: `corner_sm` (8dp)
- Altura: 56dp

### Card

**Card Padrão:**
```
background: color_surface
corner: corner_md (12dp)
elevation: 2dp
padding: space_md (16dp)
```

**Card de Aluno:**
```
layout: horizontal
avatar: 40dp circle
nome: text_h5 bold
subtítulo: text_caption muted
status badge: direita
```

**Card de Treino:**
```
icone: 48dp rounded
nome: text_h4 bold
contagem de alunos: text_caption
status chip: canto superior direito
```

### BottomNavigationBar

```
height: 64dp
background: color_surface
items: 4 (Alunos, Treinos, Avisos, Home)
active_icon_color: color_accent
inactive_icon_color: color_on_surface_muted
label: text_overline
indicator: pill background accent com opacity 20%
```

**Itens de navegação (Instrutor):**
1. Home (ícone: casa)
2. Alunos (ícone: pessoa/grupo)
3. Treinos (ícone: lista/corrida)
4. Avisos (ícone: sino/megafone)

**Itens de navegação (Aluno):**
1. Home (ícone: casa)
2. Treinos (ícone: corrida)
3. Turmas (ícone: grupo)
4. Avisos (ícone: sino)

### Avatar
```
tamanhos: 32dp, 40dp, 48dp, 64dp, 96dp
shape: circle
fallback: iniciais do nome (background gradient_primary, texto branco)
border: 2dp color_accent (quando instrutor)
```

### Chip / Badge
```
height: 24dp
padding_horizontal: 8dp
corner: corner_full
text: text_overline
variantes: success, warning, error, info, neutral
```

### ProgressBar
```
height: 8dp
corner: corner_full
track_color: color_surface_variant
progress_color: color_accent
animação: easeInOut 300ms
```

### SearchBar
```
height: 48dp
background: color_surface_variant
corner: corner_md
leading_icon: ícone de busca (muted)
clear_button: aparece quando há texto
placeholder: text_body muted
```

### FAB (Floating Action Button)
```
size: 56dp
background: color_accent
icon_color: #000000
corner: corner_full
elevation: 12dp
```

---

## 4.8 Ícones

- **Biblioteca:** Material Symbols (versão outlined)
- **Tamanhos de ícone:** 16dp, 20dp, 24dp, 32dp
- **Cor padrão:** herda do contexto (branco em dark, preto em light)
- **Cor de ação:** `color_accent`

**Ícones-chave do app:**
| Elemento | Ícone Material |
|---|---|
| Home | `home` |
| Alunos | `group` |
| Treinos | `fitness_center` |
| Avisos | `notifications` |
| Turmas | `groups` |
| Adicionar | `add` |
| Buscar | `search` |
| Editar | `edit` |
| Configurações | `settings` |
| Seta/Avançar | `arrow_forward` |
| Check/Completo | `check_circle` |
| Corrida | `directions_run` |
| Calendário | `calendar_today` |
| Pagamento | `payments` |
| Progresso | `trending_up` |

---

## 4.9 Animações e Transições

| Tipo | Duração | Easing | Uso |
|---|---|---|---|
| Transição de tela | 300ms | FastOutSlowIn | Navegação entre Activities/Fragments |
| Fade in de conteúdo | 200ms | Linear | Carregamento de listas |
| Scale press | 100ms | EaseIn | Feedback de toque em botões |
| Bottom sheet | 350ms | DecelerateInterpolator | Abertura de painéis inferiores |
| Skeleton loading | loop 1000ms | EaseInOut | Placeholder de carregamento |

### Shared Element Transitions
- Avatar do aluno → Tela de perfil
- Card de treino → Detalhe do treino

---

## 4.10 Acessibilidade

- **Contraste mínimo:** 4.5:1 para textos body, 3:1 para textos grandes
- **Touch target mínimo:** 48x48dp para todos os elementos interativos
- **Content descriptions** em todos os ícones sem label visível
- **TalkBack** compatibilidade obrigatória
- **Tamanho de fonte dinâmico:** suportar até 150% de escala sem quebrar layout
- **Cores não são o único indicador de estado** (usar ícones + texto + cor)
