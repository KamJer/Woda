# Woda

## Overview

Woda to aplikacja na Android OS do zapisywania spożytych napojów oraz ich typów. Woda pozwala na:
 - zapisywanie ilości napojów
 - definiowanie typów napojów
 - ustawienie notyfikacji mających na celu przypomnienie o konieczności nawadniania

## Prerequisites

Aby skompilować i wykorzytsać projekt będą konieczne: 
 - Android Studio z zainstalowanym sdk
 - Urządzenie z Android OS o minimalnym sdk 26

## Installation

1. sklonuj lub pobierz repozystorium
2. zaimportuj projekt do Android Studio
3. wygeneruj apk przy użyciu opcji "Build" -> "Build Bandle(s)/APK(s)"
4. zaimportuj apk na swoje urządzenie 
5. zainstaluj aplikację

## Usage

Aplikacja po otwarciu uruchomi główny widok.

### Główny widok

{???}

Główny widok zawiera:
1. Przycisk Kalendarza (lewy górny róg)
2. Przycisk Użytkownika (prawy górny róg, pierwszy)
3. Przycisk Notyfikacji (prawy górny róg, drugi)

Ponadto zawiera graficzną reprezentację wypitych napojów oraz pasek postępu zawierający ustawiony na konkretny dzień cel

#### Dodawanie/Usuwanie wypitych napojów

Po kliknięciu przyciski "+" lub "-" w dolnej cześci ekranu zostanie wyświetlony dialog proszący użytkownika o podanie objętości 
wypitego napoju lub napoju do usinięcia oraz jego typu. 

<???>

### Okno Kalendarza

Widok kalendarza zawiera informację dotyczące wszystkich wprowadzonych do tej pory napojów i pokazuje je w postaci kolendarza. 

<???>

Ilość wypitych napojów zakodowana jest kolorystycznie. 
1. Zielony: w pełni osiągnięty cel na dany dzień
2. Czerwony: brak wprowadzanie jakikolwiek danych  

W przypadku danych pośrednich, aplikacja interpoluje kolory pomiędzy czerwonym a zielonym

Aplikacja po kliknięciu w dany dzieńautomatycznie przeniesie użtkownika do głównego widoku z wybraną datą

### Okno Użytkownika

Widok pozwala na ustalenie celu do wypicia w obecnie wybranym dniu oraz wszytskich nastepnych oraz na zdefiniowanie typów napojów użytkownika. 
<???>

### Okno Notyfikacji

Okno notyfikacji pozwala na zdefiniowanie czy, w jakiej częstotliwości oraz w jakim okresie notyfikacje mają się nie pojawiać.
<???> 
