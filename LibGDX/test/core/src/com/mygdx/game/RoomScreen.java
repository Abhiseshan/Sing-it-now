package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import javax.xml.soap.Text;

public class RoomScreen implements Screen{

    Skin skin;
    Stage stage;
    SpriteBatch batch;

    private BitmapFont roomFont;
    private Texture phone;
    private Texture background;
    Texture texture = new Texture(Gdx.files.internal("test1.png"));
    private BitmapFont text;
    TextButton textButton;

    Game g;

    int buttony = 344;

    public RoomScreen(Game g){
        create();
        this.g=g;
    }

    public void create(){

        batch = new SpriteBatch();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        background = new Texture("bg_album_art.jpg");
        phone = new Texture("iPhone.png");
        roomFont = new BitmapFont(Gdx.files.internal("test1.fnt"), new TextureRegion(texture), false);
        roomFont.setColor(Color.WHITE);
        roomFont.getData().setScale(4);
        text = new BitmapFont();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();
        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLUE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        // Store the default libgdx font under the name "default".
        BitmapFont bfont=new BitmapFont();
        bfont.getData().setScale(1);
        skin.add("default",bfont);

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
        textButton=new TextButton("CONTINUE",textButtonStyle);
        textButton.setSize(200, 80);
        textButton.setPosition(583, buttony);
        stage.addActor(textButton);
        stage.addActor(textButton);
        stage.addActor(textButton);

        // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
        // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
        // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
        // revert the checked state.
        textButton.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("Clicked! Is checked: " + textButton.isChecked());
                textButton.setText("Starting new game");
                g.setScreen( new SelectionScreen(g));
            }
        });
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        if (buttony >= 300 && buttony <= 344)
            buttony--;

        textButton.setPosition(583, buttony);

        //Textures
        batch.draw(background, 0, 0);
        batch.draw(phone, 950, 100);

        if (buttony <= 300)
            roomFont.draw(batch, MyGdxGame.RoomID, 450, 600);

        //Font
        batch.draw(new Texture("instructions.png"), 100, 80);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        stage.setDebugAll(true);
    }

    @Override
    public void resize(int width, int height){
    }

    @Override
    public void show(){

    }

    @Override
    public void hide(){

    }

    @Override
    public void dispose(){
    }

    @Override
    public void pause(){

    }

    @Override
    public void resume(){

    }
}
