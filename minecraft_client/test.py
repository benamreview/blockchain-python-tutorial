from mcpi.minecraft import Minecraft
# import requests
from time import sleep
blockID = 20
# blockID = 57

mc = Minecraft.create()
# mc.player.setTilePos(0,0,0)
mc.postToChat("Created a BIG Canvas!")
# mc.postToChat(mc.player.getPos())

# response = requests.get("https://blockchain-main.ngrok.io/chain")
# chain = response.json()['chain']

# for block in chain:
#     mc.postToChat(block["nonce"])

x, y, z = mc.player.getPos()
mc.setBlock(x, y, z, blockID)
for i in range(20):
    for j in range(20):
        mc.setBlock(x+i, y+j, z, blockID)
# while True:
#     x, y, z = mc.player.getPos()
#     mc.setBlock(x, y, z, 5)
#     sleep(0.1)
print()