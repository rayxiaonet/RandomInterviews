# Credit Card Paser & Validator API

## Problem

Create an API focused around parsing and validating credit card numbers.

## Phase 1

Make an endpoint that takes a credit card number as an input, parses and validates the number, and returns the parse information if the number is valid.

The input should:

- be a string
- only contain digits
- be exactly 16 characters long

Parsing the card should return:

- IIN
- Industry (identified by the MII)
- Account Number
- Check digit

Information about parsing card numbers: [Link](https://chargebacks911.com/bank-identification-numbers/)

## Phase 2

The card must pass the Luhn Algorithm.

Luhn Algorithm: [Link](https://en.wikipedia.org/wiki/Luhn_algorithm)

Example:

Card Number = 6011000990139424
original                   : 6  | 0  | 1  | 1  | 0  | 0  | 0  | 9  | 9  | 0  | 1  | 3  | 9  | 4  | 2  | 4
doubled every other        : 12 | 0  | 2  | 1  | 0  | 0  | 0  | 9  | 18 | 0  | 2  | 3  | 18 | 4  | 4  | 4
converted to single digits : 3  | 0  | 2  | 1  | 0  | 0  | 0  | 9  | 9  | 0  | 2  | 3  | 9  | 4  | 4  | 4
sum of last row = 50
is valid = 50 % 10 == 0

## Phase 3

Make another endpoint that does not take an input but it returns a generated valid card number.

The returned card number should:

- be random (different results with every request)
- pass the luhn algorithm validation

## Other

Example numbers:

- 5555555555554444
- 4012888888881881
- 4111111111111111
- 6011000990139424
- 6011111111111117
