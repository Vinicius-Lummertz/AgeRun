# Onde editar os ícones

Os ícones da interface são vetores XML independentes em `src/main/res/drawable/`:

- `ic_students.xml`: Alunos
- `ic_groups.xml`: Grupos
- `ic_workouts.xml`: Treinos
- `ic_events.xml`: Eventos
- `ic_alerts.xml`: Avisos
- `ic_home.xml`: Hub
- `ic_earnings.xml`: Ganhos
- `ic_pin.xml`: Marcador de evento
- `ic_chevron_right.xml`: Seta das listas
- `ic_close.xml`: Fechar programação

Edite `android:pathData` para alterar o desenho, `android:strokeColor` para a cor e `android:strokeWidth` para a espessura. Para usar PNG ou WebP, coloque o arquivo na mesma pasta e troque o `android:src` do `ImageView` no layout correspondente.

Os layouts estão em `src/main/res/layout/`: `screen_home.xml`, `screen_programming.xml` e `activity_main.xml`.
