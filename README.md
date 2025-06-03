Так, ты, чучело, как-то быстро забываются пути

Регистрация (POST):
http://localhost:8080/auth/register

Вход в аккаунт (POST):
http://localhost:8080/auth/login

Достать все коворкинги - навигационная страница (GET):
http://localhost:8080/coworkings

Достать коворкинг - перейти по коворкингу (GET):
http://localhost:8080/coworkings/{coworkingId}

Бронирование места - уже нахожусь в коворкинге и бронирую место (POST):
http://localhost:8080/coworkings/{coworkingId}/{seatId}

Профиль:

Перенос бронирования (POST):
http://localhost:8080/user/coworkings/{coworkingId}

Отмена бронирования (PATCH):
http://localhost:8080/user/coworkings/{coworkingId}

///TODO: ты по-любому забудешь, но в личном кабинете крч будет справа статичное
вертикальное поле, в котором можно будет зайти в свой профиль, бронирования и
в самом низу выйти из акка, вспомни, как это будет выглядеть 

Админу пропишу позже
