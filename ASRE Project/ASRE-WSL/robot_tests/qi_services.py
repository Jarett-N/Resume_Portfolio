import qi

session = qi.Session()
session.connect("tcp://10.35.6.242:9559")

services = session.services()

# Handle both old (list[str]) and new (list[dict]) formats
if services and isinstance(services[0], dict):
    names = [s['name'] for s in services if 'name' in s]
else:
    names = services

print("\nAvailable Qi services:")
for name in sorted(names):
    print(" -", name)
