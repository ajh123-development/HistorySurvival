package net.ddns.minersonline.HistorySurvival.network;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.AttributeKey;
import net.ddns.minersonline.HistorySurvival.BrokenHash;
import net.ddns.minersonline.HistorySurvival.Game;
import net.ddns.minersonline.HistorySurvival.NettyServer;
import net.ddns.minersonline.HistorySurvival.api.auth.GameProfile;
import net.ddns.minersonline.HistorySurvival.api.ecs.GameObject;
import net.ddns.minersonline.HistorySurvival.api.ecs.MeshComponent;
import net.ddns.minersonline.HistorySurvival.api.ecs.TransformComponent;
import net.ddns.minersonline.HistorySurvival.api.registries.ModelType;
import net.ddns.minersonline.HistorySurvival.api.registries.Registries;
import net.ddns.minersonline.HistorySurvival.engine.GameObjectManager;
import net.ddns.minersonline.HistorySurvival.engine.entities.ControllableComponent;
import net.ddns.minersonline.HistorySurvival.network.packets.AlivePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.DisconnectPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.EncryptionResponsePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.HandshakePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.client.LoginStartPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.EncryptionRequestPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.auth.server.LoginSuccessPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.client.StartPingPacket;
import net.ddns.minersonline.HistorySurvival.network.packets.server.JoinGamePacket;
import net.ddns.minersonline.HistorySurvival.network.packets.server.PingResponsePacket;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

	private final ChannelGroup channelGroup;

	public ServerHandler(ChannelGroup channelGroup) {
		Utils.ENCRYPTION_MODE = Utils.EncryptionMode.NONE;
		this.channelGroup = channelGroup;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelGroup.add(ctx.channel());
		ctx.channel().attr(AttributeKey.valueOf("state")).set(-1);
		ctx.channel().attr(AttributeKey.valueOf("userName")).set(null);
		ctx.channel().attr(AttributeKey.valueOf("object")).set(null);
	}

	@Override
	public void channelInactive(@NotNull ChannelHandlerContext ctx) {
		channelGroup.remove(ctx.channel());
		GameObject go = (GameObject) ctx.channel().attr(AttributeKey.valueOf("object")).get();
		if (go != null) {
			GameObjectManager.removeGameObject(go);
		}
		ctx.channel().attr(AttributeKey.valueOf("object")).set(null);
	}

	@Override
	public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg){
		Packet requestData = (Packet) msg;

		if(requestData.getOwner().equals(Utils.GAME_ID)) {
			if(requestData.getId().equals("handshake")) {
				Packet responseData = new Packet(requestData);
				HandshakePacket handshakePacket = Packet.cast(responseData, HandshakePacket.class);
				if (handshakePacket != null) {
					ctx.channel().attr(AttributeKey.valueOf("state")).set(handshakePacket.getNextState());
				}
			}
			if(requestData.getId().equals("test")) {
				Packet responseData = new Packet(requestData);
				ctx.writeAndFlush(responseData);
			}
			if(requestData.getId().equals("alive")) {
				Packet responseData = new AlivePacket();
				ctx.writeAndFlush(responseData);
			}
			if (((Integer)ctx.channel().attr(AttributeKey.valueOf("state")).get()) == 1){
				if (requestData.getId().equals("startPing")) {
					ctx.writeAndFlush(new PingResponsePacket(
						"Placing pickles on the sea floor!"
					));
					ctx.close();
				}
			}
			if(((Integer)ctx.channel().attr(AttributeKey.valueOf("state")).get()) == 2){
				if(requestData.getId().equals("loginStart")) {
					Packet responseData = new Packet(requestData);
					LoginStartPacket loginStartPacket = Packet.cast(responseData, LoginStartPacket.class);
					if (loginStartPacket != null) {
						ctx.channel().attr(AttributeKey.valueOf("userName")).set(loginStartPacket.getName());

						ctx.writeAndFlush(new EncryptionRequestPacket(
								NettyServer.serverId,
								NettyServer.publicKey.getEncoded(),
								NettyServer.verifyToken
						));
					}
				}
				if (requestData.getId().equals("encryptionResponse")) {
					Packet responseData = new Packet(requestData);
					EncryptionResponsePacket encryptionResponsePacket = Packet.cast(responseData, EncryptionResponsePacket.class);
					if (encryptionResponsePacket != null) {
						if (encryptionResponsePacket.getVerifyToken().equals(NettyServer.verifyToken)) {
							HttpClient httpClient = HttpClientBuilder.create().build();
							boolean loggedIn = false;
							GameProfile profile = null;
							try {
								Cipher cipher = Cipher.getInstance(Utils.ENC_ALGO);
								cipher.init(Cipher.DECRYPT_MODE, NettyServer.privateKey);
								byte[] dec_secret = cipher.doFinal(encryptionResponsePacket.getSharedSecret());
								String secret = new String(dec_secret, StandardCharsets.UTF_8);

								String hash = BrokenHash.hash(NettyServer.serverId+
										secret+
										Arrays.toString(NettyServer.publicKey.getEncoded())
								);

								String userName = (String) ctx.channel().attr(AttributeKey.valueOf("userName")).get();
								String URL = Utils.HAS_JOINED_URL+"?username="+userName+"&serverId="+hash;
								HttpGet request = new HttpGet(URL);
								HttpResponse response = httpClient.execute(request);

								String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
								if (!responseBody.equals("{}")){
									loggedIn = true;
									Gson gson = new Gson();
									profile = gson.fromJson(responseBody, GameProfile.class);
									Utils.ENCRYPTION_MODE = Utils.EncryptionMode.SERVER;
									Utils.ENC_PRIVATE = NettyServer.privateKey;
									Utils.ENC_PUBLIC = NettyServer.publicKey;

									ctx.writeAndFlush(new LoginSuccessPacket(
											profile.getName(),
											profile.getID().toString()
									));

								} else {
									ctx.writeAndFlush(new DisconnectPacket(
											"Unable to login",
											"Authentication Error",
											null
									));
									ctx.close();
								}

							} catch (Exception ignored) {}

							if(loggedIn && profile != null) {
								logger.info("UUID of player "+profile.getName()+" is "+profile.getID());
								String remote = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();
								int port = ((InetSocketAddress)ctx.channel().remoteAddress()).getPort();

								GameObject player = new GameObject();
								player.addComponent(new TransformComponent());
								player.addComponent(new MeshComponent(ModelType.PLAYER_MODEL.create()));
								player.addComponent(new ControllableComponent());
								GameObjectManager.addGameObject(player);
								ctx.channel().attr(AttributeKey.valueOf("object")).set(player);

								TransformComponent pos = player.getComponent(TransformComponent.class);
								if (pos != null) {
									logger.info(profile.getName()+"[/"+remote+":"+port+"] logged in with entity id "+player.getId()+" at "+pos.position);
									ctx.channel().attr(AttributeKey.valueOf("entityId")).set(player.getId());

									ctx.writeAndFlush(new JoinGamePacket(
											player
									));
								} else {
									ctx.writeAndFlush(new DisconnectPacket(
											"Incorrect GameObject",
											"NullPointerException: player.pos is null",
											null
									));
									ctx.close();
								}

								try {
									Thread.sleep(10);
								} catch (InterruptedException ignored) {}
								ctx.channel().attr(AttributeKey.valueOf("state")).set(3);
							} else {
								ctx.writeAndFlush(new DisconnectPacket(
										"Incorrect profile data",
										"Authentication Error",
										null
								));
								ctx.close();
							}
						} else {
							ctx.writeAndFlush(new DisconnectPacket(
									"Incorrect verify token",
									"Authentication Error",
									null
							));
							ctx.close();
						}
					} else {
						ctx.writeAndFlush(new DisconnectPacket(
								"Malformed encryption response",
								"Authentication Error",
								null
						));
						ctx.close();
					}
				}
			}
		}
	}
}