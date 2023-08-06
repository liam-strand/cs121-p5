# MetroSim2

My simulation has two types of concurrent threads:

First, the `Journey` class represents a `Passenger`'s movement through the stations in its journey. The passenger begins at its starting `Station`. If that `Station` has multiple `Line`s stopping at it, the `Passenger` decides which `Line`'s `Platform` to wait at. When the `Line` arrives, it signals the `Journey`'s on its platform to board the `Line`, and the `Journey` selects the appropriate `Car` in the `Line` to wait on. When the `Line` arrives at that `Car`'s `Station`, the `Line` signals the `Journey` to exit the train and wait on the `Platform`, and the cycle continues. 

Second, the `Line` class represents a `Train`'s movement through its stations. Each `Line` begins by acquiring the lock on its starting `Station`. Then, when it attempts to move to its next `Station`, it waits to acquire the lock on the next `Station` before moving. This way, when the `Line` moves, it has a lock both on the `Station` it is leaving and on the station it is entering. After the `Line` moves, it notifies the `Journey`s waiting to exit the `Line` at the current `Station`, then notifies the `Journey`s waiting in the `Station` to board this particular `Line`.

In summary, I use a lock on each `Station` to make sure only one `Train` can be in a `Station` at a time, and I use condition variables with `.wait()` and `.notifyAll()` to signal the `Passengers` to move through the `Station`s and `Line`s.