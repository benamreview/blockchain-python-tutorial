from mcpi.minecraft import Minecraft
import requests
from time import sleep
blockID = 42
mc = Minecraft.create()
# mc.player.setTilePos(0,0,0)
mc.postToChat("Hello World!")
# mc.postToChat(mc.player.getPos())

# response = requests.get("https://blockchain-main.ngrok.io/chain")
# chain = response.json()['chain']

# for block in chain:
#     mc.postToChat(block["nonce"])

x, y, z = mc.player.getPos()
mc.setBlock(x, y, z, blockID)
# while True:
#     x, y, z = mc.player.getPos()
#     mc.setBlock(x, y, z, 5)
#     sleep(0.1)
print()