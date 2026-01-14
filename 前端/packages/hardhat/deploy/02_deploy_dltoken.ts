import { HardhatRuntimeEnvironment } from "hardhat/types";
import { DeployFunction } from "hardhat-deploy/types";
import { Contract } from "ethers";

/**
 * Deploys a contract named "DLToken" using the deployer account.
 *
 * @param hre HardhatRuntimeEnvironment object.
 */
const deployDLToken: DeployFunction = async function (hre: HardhatRuntimeEnvironment) {
  /*
    On localhost, the deployer account is the one that comes with Hardhat, which is already funded.

    When deploying to live networks (e.g `yarn deploy --network sepolia`), the deployer account
    should have sufficient balance to pay for the gas fees for contract creation.
  */
  const { deployer } = await hre.getNamedAccounts();
  const { deploy } = hre.deployments;

  await deploy("ERC20Dl202330550291", {
    from: deployer,
    // Contract constructor arguments
    args: [],
    log: true,

    autoMine: true,
  });

  // Get the deployed contract to interact with it after deploying.
  const dlToken = await hre.ethers.getContract<Contract>("ERC20Dl202330550291", deployer);
  console.log("ERC20Dl202330550291 deployed to:", await dlToken.getAddress());
};

export default deployDLToken;

// Tags are useful if you have multiple deploy files and only want to run one of them.
// e.g. yarn deploy --tags DLToken
deployDLToken.tags = ["ERC20Dl202330550291"];