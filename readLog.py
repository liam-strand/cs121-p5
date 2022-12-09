"""readLog.py

By: Liam Strand
On: December 2022
"""

from pprint import pprint

def main():
    with open("./test.txt", "r") as f:
        file = f.readlines()

    lines: dict[str, list[str]] = {}
    passengers: dict[str, list[str]] = {}
    
    log: list[str] = []
    pass_names: set[str] = set()
    train_names: set[str] = set()
    station_names: set[str] = set()

    for row in file:
        processRowForConfig(row, lines, passengers)
        processRowForLog(row, log, pass_names, train_names, station_names)

    pprint(lines)
    pprint(passengers)

    writeLog("./test.txt", log, pass_names, train_names, station_names)


def processRowForConfig(
    row: str,
    lines: dict[str, list[str]],
    passengers: dict[str, list[str]],
):
    words = row.split()
    if (words[0] == "Passenger"):
        name = words[1]
        train = words[3]
        station = words[5]

        if (not name in passengers.keys()):
            passengers[name] = []
        
        passengers[name].append(station)
    
    if (words[0] == "Train"):
        train = words[1]
        s1 = words[4]
        s2 = words[6]

        if (not train in lines.keys()):
            lines[train] = []
        
        lines[train].append(s1)

def processRowForLog(row: str, log: list[str], pass_names: set[str], train_names: set[str], station_names: set[str]):
    words = row.split()
    if (words[0] == "Passenger"):
        name = words[1]
        action = words[2]
        train = words[3]
        station = words[5]

        pass_names.add(name)
        train_names.add(train)
        station_names.add(station)
        
        if (action == "boards"):
            log.append(f"log.passenger_boards({name}, {train}, {station});")
        else:
            log.append(f"log.passenger_deboards({name}, {train}, {station});")
    
    if (words[0] == "Train"):
        train = words[1]
        s1 = words[4]
        s2 = words[6]

        train_names.add(train)
        station_names.add(s1)
        station_names.add(s2)

        log.append(f"log.train_moves({train}, {s1}, {s2});")

def writeLog(file: str, log: list[str], pass_names: set[str], train_names: set[str], station_names: set[str]):

    with open(file, "w") as f:
        for name in pass_names:
            f.write(f"Passenger {name} = Passenger.make(\"{name}\");\n")
        for name in train_names:
            f.write(f"Train {name} = Train.make(\"{name}\");\n")
        for name in station_names:
            f.write(f"Station {name} = Station.make(\"{name}\");\n")

        for entry in log:
            f.write(f"{entry}\n")
        

if __name__ == "__main__":
    main()
