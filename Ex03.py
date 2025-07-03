import random

name = input("What is your name?\n> ")
print(f"Hello, {name}!")

print("Rolling the dice...")
d1, d2 = random.randint(1, 6), random.randint(1, 6)
total = d1 + d2
print(f"Die 1: {d1}")
print(f"Die 2: {d2}")
print(f"Total value: {total}")