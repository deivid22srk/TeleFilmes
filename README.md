# TeleFilmes

📺 Organizador de vídeos do Telegram por séries, temporadas e episódios.

## 📱 Sobre o App

TeleFilmes é um aplicativo Android que permite organizar vídeos do Telegram de forma estruturada, agrupando-os em:
- **Séries**: Coleções principais de conteúdo
- **Temporadas**: Divisões dentro de cada série
- **Episódios**: Vídeos individuais salvos do Telegram

## ✨ Funcionalidades

- ✅ Criar e gerenciar séries personalizadas
- ✅ Organizar temporadas dentro de cada série
- ✅ Salvar vídeos do Telegram como episódios
- ✅ Autenticação com Telegram via número de telefone
- ✅ Navegar pelos chats do Telegram
- ✅ Interface moderna com Material Design 3
- ✅ Banco de dados local com Room

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Arquitetura**: MVVM (Model-View-ViewModel)
- **Banco de Dados**: Room
- **Navegação**: Navigation Compose
- **Telegram**: TDLib (Telegram Database Library)
- **Corrotinas**: Kotlin Coroutines + Flow

## 📋 Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 17
- Android SDK 24 ou superior
- Gradle 8.2

## 🚀 Como Compilar

1. Clone o repositório:
```bash
git clone https://github.com/deivid22srk/TeleFilmes.git
cd TeleFilmes
```

2. Abra o projeto no Android Studio

3. Sincronize o Gradle:
```bash
./gradlew build
```

4. Execute no emulador ou dispositivo físico

## 📦 Build via GitHub Actions

O projeto está configurado com CI/CD usando GitHub Actions. A cada push na branch `main`, o APK debug é gerado automaticamente.

Para baixar o APK gerado:
1. Vá em **Actions** no GitHub
2. Selecione o workflow mais recente
3. Baixe o artifact **TeleFilmes-Debug-APK**

## 🔑 Configuração do Telegram

O app usa as credenciais da API do Telegram. Por padrão, está usando valores de exemplo:
- **API ID**: 94575
- **API Hash**: a3406de8d171bb422bb6ddf3bbd800e2

⚠️ **Importante**: Para uso em produção, obtenha suas próprias credenciais em https://my.telegram.org/apps

Para configurar suas credenciais:
1. Vá em `app/src/main/java/com/telefilmes/app/telegram/TelegramClient.kt`
2. Substitua `apiId` e `apiHash` pelos seus valores

## 📱 Como Usar

1. **Primeira vez**: Faça login com seu número do Telegram
2. **Criar Série**: Na tela inicial, clique no botão "+"
3. **Adicionar Temporadas**: Entre na série e adicione temporadas
4. **Salvar Vídeos**: 
   - Clique no botão de chat
   - Navegue até o chat desejado
   - Selecione vídeos e salve na temporada desejada

## 📂 Estrutura do Projeto

```
app/
├── data/
│   ├── dao/           # Data Access Objects
│   ├── database/      # Room Database
│   ├── model/         # Modelos de dados
│   └── repository/    # Repositórios
├── telegram/          # Integração com Telegram
├── ui/
│   ├── navigation/    # Navegação
│   ├── screen/        # Telas Compose
│   ├── theme/         # Temas Material 3
│   └── viewmodel/     # ViewModels
├── MainActivity.kt
└── TeleFilmesApplication.kt
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:
1. Fazer fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abrir um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autor

Desenvolvido por [@deivid22srk](https://github.com/deivid22srk)

## 🐛 Problemas Conhecidos

- A integração com TDLib requer a biblioteca nativa `libtdjni.so`
- Alguns recursos do Telegram podem estar limitados
- A funcionalidade de salvar vídeos ainda está em desenvolvimento

## 🔮 Roadmap

- [ ] Reprodutor de vídeo integrado
- [ ] Download de vídeos para offline
- [ ] Suporte a filmes (sem temporadas)
- [ ] Pesquisa e filtros
- [ ] Temas personalizados
- [ ] Backup e restauração
- [ ] Widget para tela inicial

## 📞 Suporte

Para suporte, abra uma issue no GitHub ou entre em contato através das issues do projeto.
