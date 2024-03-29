# DungeonTrader

## Информация

Minecraft 1.20

Зависимости: [CommandAPI](https://commandapi.jorel.dev/)

Вспомогательный плагины: [ExecutableItems](https://www.spigotmc.org/resources/custom-items-plugin-executable-items.77578/), SCore

## Команды

### Призвать жителя

Варианты команд: ```/dungeontradersummonvillage```, ```/dtsummonvillage```, ```/dungeontradersv```, ```/dtsv```

Использование: ```/dtsv <имя жителя>```

Описание: Спавнит жителя с настройками из конфига.

### Заменить жителя

Варианты команд: ```/dungeontraderrefreshvillage```, ```/dtrefreshvillage```, ```/dungeontraderrv```, ```/dtrv```

Использование: ```/dtrv <сущность>```

Описание: Убивает предыдущего жителя и на его месте спавнит нового.

### Перезагрузить конфиг

Варианты команд: ```/dungeontraderreload```, ```/dtreload```

Использование: ```/treload <сущность>```

Описание: Перезагружает конфиг с жителями.

## Конфиг

```yml
villagers:
  # имя жителя (нужно для призыва)
  example:
    # Тип жителя https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Villager.Type.html
    type: "plains"
    # Профессия жителя https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/entity/Villager.Profession.html
    profession: "weaponsmith"
    # Уровень жителя
    level: "5"
    # Количество сделок. Выбираются случайные из "recipes" ниже
    amountRecipes: 1

    # Настройки жителя
    CanPickUpLoot: false
    Glowing: false
    HasVisualFire: false
    Invulnerable: true
    NoAI: true
    NoGravity: true
    PersistenceRequired: false
    Silent: true

    # Дополнительные теги жителя (нужны для исползования командных блоков)
    Tags:
      test: k1k

    # Список всех сделок
    recipes:
      # Номер сделки
      1:
        # Максимальное кол-во использований торга
        maxUses: 3
        # Получает ли игрок опыт за торговлю
        rewardExp: false

        # Предмет покупки первого слота
        buyA:
          item: "SNOW"
          count: 1
          customModelData: 1

        # Предмет покупки вторго слота
        buyB:
          item: "YELLOW_WOOL"
          count: 12
          customModelData: 1

        # Предмет на продажу
        sell:
          item: "BEDROCK"
          count: 44
          customModelData: 1
      1:
        maxUses: 1
        rewardExp: false

        buyA:
          item: "GOLD_NUGGET"
          # count может быть случайным числом в диапозоне
          count:
            min: 10
            max: 20

        sell:
          # Можно использовать кастомные предметы из ExecutableItems. Для этого нужно установить параметр "ei_mode" на  "true", а в поле "item" указать название кастомного предмета
          ei_mode: true
          item: "Stun_Ball"
          count: 1
```