% Verifica se il valore appartiene a un insieme valido
valid_branch(A) :- member(A, [a, b, c]).
valid_customer_type(M) :- member(M, [member, normal]).
valid_gender(G) :- member(G, [male, female]).
valid_product_line(P) :- member(P, [electronic_accessories, cosmetics, food_and_beverages, sports_and_travel, home_and_lifestyle, fashion]).
valid_payment(P) :- member(P, [cash, credit_card, ewallet]).

% Verifica se i valori numerici sono coerenti
numeric_values_consistent(UnitPrice, Quantity, Tax, Total, Cogs, GrossIncome, GrossMarginPercentage) :-
    CalculatedTotal is UnitPrice * Quantity + Tax,
    abs(CalculatedTotal - Total) =< 0.01,
    CalculatedGrossIncome is Total - Cogs,
    abs(CalculatedGrossIncome - GrossIncome) =< 0.01,
    GrossMarginPercentage >= 0,
    GrossMarginPercentage =< 100.

% Verifica se la data è valida
date_valid(Date) :-
    get_time(CurrentTime),
    stamp_date_time(CurrentTime, CurrentDateTime, 'local'),
    date_time_value(day, Date, Day),
    date_time_value(month, Date, Month),
    date_time_value(year, Date, Year),
    date_time_value(day, CurrentDateTime, CurrentDay),
    date_time_value(month, CurrentDateTime, CurrentMonth),
    date_time_value(year, CurrentDateTime, CurrentYear),
    (Year < CurrentYear;
    (Year = CurrentYear, Month < CurrentMonth);
    (Year = CurrentYear, Month = CurrentMonth, Day =< CurrentDay)).

% Verifica se il rating non è un outlier
rating_not_outlier(Rating, Mean, StdDev) :-
    Rating >= (Mean - 2 * StdDev),
    Rating =< (Mean + 2 * StdDev).
+