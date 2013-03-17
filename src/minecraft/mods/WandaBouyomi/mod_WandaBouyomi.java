package mods.WandaBouyomi;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.snowink.bouyomichan.BouyomiChan4J;
import mods.WandaCore.WandaModBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureLoadEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.CanUpdate;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IChatListener;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;

@Mod(modid = "WandaBouyomi", name = "Wanda Bouyomi", version = "0.0.1", dependencies = "required-after:WandaCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "hoge" }, connectionHandler = mod_WandaBouyomi.class, packetHandler = mod_WandaBouyomi.class)
public class mod_WandaBouyomi extends WandaModBase implements IChatListener,
		IConnectionHandler, IPacketHandler {

	public class EventHook {
		@ForgeSubscribe
		public void recieveEvent(Event event) {
			if (event instanceof LivingUpdateEvent) {
				return;
			}
			if (event instanceof CanUpdate) {
				return;
			}
			if (event instanceof PlaySoundAtEntityEvent) {
				return;
			}
			if (event instanceof RenderWorldLastEvent) {
				return;
			}
			if (event instanceof LivingFallEvent) {
				return;
			}
			if (event instanceof DrawBlockHighlightEvent) {
				return;
			}
			if (event instanceof LivingJumpEvent) {
				return;
			}
			if (event instanceof PlaySoundEvent) {
				return;
			}
			if (event instanceof PlaySoundSourceEvent) {
				return;
			}
			if (event instanceof EntityItemPickupEvent) {
				return;
			}

			if (event instanceof LivingSpawnEvent) {
				LivingSpawnEvent spawn = (LivingSpawnEvent) event;
				FMLLog.info("%sがスポーン", spawn.entityLiving.getEntityName());
				return;
			}
			if (event instanceof LivingHurtEvent) {
				LivingHurtEvent hurt = (LivingHurtEvent) event;
				FMLLog.info("%sは%sにより%dのダメージを受けた",
						hurt.entityLiving.getEntityName(),
						hurt.source.damageType, hurt.ammount);
				return;
			}
			if (event instanceof LivingAttackEvent) {
				LivingAttackEvent attack = (LivingAttackEvent) event;
				FMLLog.info("%sは%sで%dのダメージを与えた",
						attack.entityLiving.getEntityName(),
						attack.source.damageType, attack.ammount);
				return;
			}
			if (event instanceof TextureLoadEvent) {
				return;
			}
			if (event instanceof ChunkDataEvent.Save) {
				return;
			}
			FMLLog.info(event.toString());

		}
	}

	private static BouyomiChan4J bouyomi;
	private Pattern sayClient;
	private Pattern sayServer;
	private Pattern serverOperation;

	private static int volume;
	private static int speed;
	private static int tone;
	private static int voice;

	@Override
	protected String getModID() {
		return "WandaBouyomi";
	}

	@Override
	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init WandaBouyomi");
		// MinecraftForge.EVENT_BUS.register(new EventHook());
		Property propertyHost = config.get("general", "HostIP", "localhost");
		Property propertyPort = config.get("general", "Port", 50001);
		Property propertyVolume = config.get("general", "Volume", -1);
		Property propertySpeed = config.get("general", "Speed", -1);
		Property propertyTone = config.get("general", "Tone", -1);
		Property propertyVoice = config.get("general", "Voice", 0);
		config.save();

		sayClient = Pattern.compile("^\\<(\\w*)\\> (.*)");
		sayServer = Pattern.compile("^\\[Server\\] (.*)");
		serverOperation = Pattern.compile("^§7§o\\[Server: (.*)\\]");
		volume = propertyVolume.getInt();
		speed = propertySpeed.getInt();
		tone = propertyTone.getInt();
		voice = propertyVoice.getInt();

		bouyomi = new BouyomiChan4J(propertyHost.getString(),
				propertyPort.getInt());
		bouyomi.talk(volume, speed, tone, voice, "WandaBouyomiが接続しました");
		NetworkRegistry.instance().registerChatListener(this);
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		return message;
	}

	@Override
	public Packet3Chat clientChat(NetHandler handler, Packet3Chat message) {
		Matcher matcher = sayClient.matcher(message.message);
		if (matcher.find()) {
			bouyomi.talk(volume, speed, tone, voice, matcher.group(2));
		} else {
			matcher = sayServer.matcher(message.message);
			if (matcher.find()) {
				bouyomi.talk(volume, speed, tone, voice, matcher.group(1));
			} else {
				matcher = serverOperation.matcher(message.message);
				if (matcher.find()) {
					bouyomi.talk(volume, speed, tone, voice, matcher.group(1));
				}
			}
		}
		return message;
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) {
		if (clientHandler.isServerHandler()) {
			MinecraftServer server = MinecraftServer.getServer();
			List list = server.getConfigurationManager().playerEntityList;
			String name = "誰か";
			for (Object obj : list) {
				if (obj instanceof EntityPlayer) {
					EntityPlayer new_name = (EntityPlayer) obj;
					if (new_name.entityId == login.clientEntityId) {
						name = new_name.username;
						break;
					}
				}

			}
			bouyomi.talk(volume, speed, tone, voice, name + "がログインしました");
		}
	}

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {

	}

}
